package com.sofka.stepdefinitions;

import com.sofka.config.AutomationEnvironment;
import com.sofka.config.AutomationEnvironment.Credentials;
import com.sofka.questions.CodigoRespuesta;
import com.sofka.questions.ResponseField;
import com.sofka.tasks.*;
import com.sofka.utils.ActorMemoryKeys;
import com.sofka.utils.JourneyTestDataFactory;
import com.sofka.utils.JourneyTestDataFactory.JourneyData;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.thucydides.model.util.EnvironmentVariables;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;

public class FlujoPacienteStepDefinitions {

    private EnvironmentVariables environmentVariables;

    @Before
    public void setTheStage() {
        OnStage.setTheStage(new OnlineCast());
    }

    @Given("que el personal médico se autentica en el sistema")
    public void queElPersonalMedicoSeAutenticaEnElSistema() {
        Credentials validCredentials = AutomationEnvironment.validCredentials(environmentVariables);
        String baseUrl = AutomationEnvironment.apiBaseUrl(environmentVariables);
        theActorCalled("DoctorAdmin").whoCan(CallAnApi.at(baseUrl));

        theActorInTheSpotlight().attemptsTo(
            AutenticarStaff.con(validCredentials.username(), validCredentials.password())
        );
        theActorInTheSpotlight().should(seeThat("Código de login", CodigoRespuesta.deLaPeticion(), equalTo(200)));
        theActorInTheSpotlight().should(seeThat(
            "El token de acceso",
            ResponseField.<String>valueOf("accessToken"),
            allOf(notNullValue(), matchesPattern(".+"))
        ));
        theActorInTheSpotlight().should(seeThat("El usuario autenticado", ResponseField.<String>valueOf("username"), equalTo(validCredentials.username())));
        theActorInTheSpotlight().should(seeThat("El rol autenticado", ResponseField.<String>valueOf("role"), equalTo(AutomationEnvironment.expectedRole(environmentVariables))));
    }

    @When("se registra la llegada de un paciente a recepción")
    public void seRegistraLaLlegadaDeUnPacienteARecepcion() {
        JourneyData journeyData = JourneyTestDataFactory.create(environmentVariables);
        theActorInTheSpotlight().remember(ActorMemoryKeys.JOURNEY_DATA, journeyData);

        theActorInTheSpotlight().attemptsTo(
            RegistrarPaciente.conInfo(journeyData.registerPatientRequest())
        );
        theActorInTheSpotlight().should(seeThat("Código al registrar", CodigoRespuesta.deLaPeticion(), equalTo(200)));
        theActorInTheSpotlight().should(seeThat("El patientId registrado", ResponseField.<String>valueOf("patientId"), equalTo(journeyData.patientId())));
        theActorInTheSpotlight().should(seeThat("El queueId registrado", ResponseField.<String>valueOf("queueId"), equalTo(journeyData.queueId())));
        theActorInTheSpotlight().should(seeThat(
            "El turnId registrado",
            ResponseField.<String>valueOf("turnId"),
            allOf(notNullValue(), matchesPattern(".+"))
        ));
        String turnId = net.serenitybdd.rest.SerenityRest.lastResponse().jsonPath().getString("turnId");
        theActorInTheSpotlight().remember(ActorMemoryKeys.TURN_ID, turnId);
    }

    @When("el operador de caja llama al paciente para validar el pago")
    public void elOperadorDeCajaLlamaAlPacienteParaValidarElPago() {
        JourneyData journeyData = currentJourneyData();
        String turnId = currentTurnId();

        theActorInTheSpotlight().attemptsTo(
            LlamarPacienteEnCaja.conInfo(journeyData.callNextAtCashierRequest())
        );
        theActorInTheSpotlight().should(seeThat("Código al llamar en caja", CodigoRespuesta.deLaPeticion(), equalTo(200)));
        theActorInTheSpotlight().should(seeThat("El turnId en caja", ResponseField.<String>valueOf("turnId"), equalTo(turnId)));
        theActorInTheSpotlight().should(seeThat("El estado visible en caja", ResponseField.<String>valueOf("currentState"), equalTo("AtCashier")));
        theActorInTheSpotlight().should(seeThat("La caja asignada", ResponseField.<String>valueOf("cashierStationId"), equalTo(journeyData.cashierStationId())));
    }

    @When("el operador de caja valida el pago del paciente")
    public void elOperadorDeCajaValidaElPagoDelPaciente() {
        JourneyData journeyData = currentJourneyData();

        theActorInTheSpotlight().attemptsTo(
            ValidarPagoPaciente.conInfo(journeyData.validatePaymentRequest(currentTurnId()))
        );
        theActorInTheSpotlight().should(seeThat("Código al validar pago", CodigoRespuesta.deLaPeticion(), equalTo(200)));
        theActorInTheSpotlight().should(seeThat("La validacion del pago fue exitosa", ResponseField.<Boolean>valueOf("success"), is(true)));
        theActorInTheSpotlight().should(seeThat("El mensaje de pago", ResponseField.<String>valueOf("message"), equalTo("Payment validated successfully")));
    }

    @When("el administrador médica activa una de las salas")
    public void elAdministradorMedicaActivaUnaDeLasSalas() {
        JourneyData journeyData = currentJourneyData();

        theActorInTheSpotlight().attemptsTo(
            ActivarConsultorio.conInfo(journeyData.activateRoomRequest())
        );
        theActorInTheSpotlight().should(seeThat("Código al activar sala", CodigoRespuesta.deLaPeticion(), equalTo(200)));
        theActorInTheSpotlight().should(seeThat("La activacion fue exitosa", ResponseField.<Boolean>valueOf("success"), is(true)));
        theActorInTheSpotlight().should(seeThat("El mensaje de activacion", ResponseField.<String>valueOf("message"), equalTo("Consulting room " + journeyData.roomName() + " activated")));
    }

    @When("el doctor llama al siguiente paciente en la sala")
    public void elDoctorLlamaAlSiguientePacienteEnLaSala() {
        JourneyData journeyData = currentJourneyData();
        String turnId = currentTurnId();

        theActorInTheSpotlight().attemptsTo(
            LlamarPaciente.conInfo(journeyData.medicalCallNextRequest())
        );
        theActorInTheSpotlight().should(seeThat("Código al reclamar turno", CodigoRespuesta.deLaPeticion(), equalTo(200)));
        theActorInTheSpotlight().should(seeThat("El turnId llamado a consulta", ResponseField.<String>valueOf("turnId"), equalTo(turnId)));
        theActorInTheSpotlight().should(seeThat("El consultorio llamado", ResponseField.<String>valueOf("consultingRoomId"), equalTo(journeyData.roomId())));
        theActorInTheSpotlight().should(seeThat("El estado visible en consulta", ResponseField.<String>valueOf("currentState"), equalTo("Called")));
    }

    @When("el doctor inicia la consulta médica")
    public void elDoctorIniciaLaConsultaMedica() {
        JourneyData journeyData = currentJourneyData();

        theActorInTheSpotlight().attemptsTo(
            IniciarConsulta.conInfo(journeyData.startConsultationRequest(currentTurnId()))
        );
        theActorInTheSpotlight().should(seeThat("Código al iniciar consulta", CodigoRespuesta.deLaPeticion(), equalTo(200)));
        theActorInTheSpotlight().should(seeThat("El inicio de consulta fue exitoso", ResponseField.<Boolean>valueOf("success"), is(true)));
        theActorInTheSpotlight().should(seeThat("El mensaje de inicio", ResponseField.<String>valueOf("message"), equalTo("Consultation started")));
    }

    @When("el doctor finaliza la consulta médica exitosamente")
    public void elDoctorFinalizaLaConsultaMedicaExitosamente() {
        JourneyData journeyData = currentJourneyData();
        String turnId = currentTurnId();

        theActorInTheSpotlight().attemptsTo(
            FinalizarConsulta.conInfo(journeyData.finishConsultationRequest(turnId))
        );
        theActorInTheSpotlight().should(seeThat("Código al finalizar consulta", CodigoRespuesta.deLaPeticion(), equalTo(200)));
        theActorInTheSpotlight().should(seeThat("La finalizacion fue exitosa", ResponseField.<Boolean>valueOf("success"), is(true)));
        theActorInTheSpotlight().should(seeThat("El mensaje final", ResponseField.<String>valueOf("message"), equalTo("Consultation completed")));
    }

    @Then("el paciente debe figurar como atendido en el sistema")
    public void elPacienteDebeFigurarComoAtendidoEnElSistema() {
        theActorInTheSpotlight().should(seeThat("La consulta queda cerrada con exito", ResponseField.<Boolean>valueOf("success"), is(true)));
        theActorInTheSpotlight().should(seeThat("El backend confirma el cierre", ResponseField.<String>valueOf("message"), equalTo("Consultation completed")));
    }

    private JourneyData currentJourneyData() {
        return theActorInTheSpotlight().recall(ActorMemoryKeys.JOURNEY_DATA);
    }

    private String currentTurnId() {
        String turnId = theActorInTheSpotlight().recall(ActorMemoryKeys.TURN_ID);

        if (turnId == null || turnId.isBlank()) {
            throw new IllegalStateException("The registration response did not provide a turnId; the flow cannot continue reliably.");
        }

        return turnId;
    }
}
