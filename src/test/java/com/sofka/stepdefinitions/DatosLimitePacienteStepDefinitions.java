package com.sofka.stepdefinitions;

import com.sofka.config.AutomationEnvironment;
import com.sofka.config.AutomationEnvironment.Credentials;
import com.sofka.models.RegisterPatientRequest;
import com.sofka.questions.CodigoRespuesta;
import com.sofka.tasks.AutenticarStaff;
import com.sofka.tasks.RegistrarPaciente;
import com.sofka.utils.ActorMemoryKeys;
import com.sofka.utils.ApiRequestSupport;
import com.sofka.utils.Endpoints;
import com.sofka.utils.JourneyTestDataFactory;
import com.sofka.utils.JourneyTestDataFactory.JourneyData;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.serenitybdd.screenplay.rest.interactions.Post;
import net.thucydides.model.util.EnvironmentVariables;

import java.util.UUID;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;

public class DatosLimitePacienteStepDefinitions {

    private EnvironmentVariables environmentVariables;
    private JourneyData lastJourneyData;

    @When("se intenta registrar un paciente con nombre {string} y prioridad {string} y notas {string}")
    public void seIntentaRegistrarConDatosLimite(String nombre, String prioridad, String notas) {
        // Arrange — datos de boundary/equivalence
        String queueId = "QUEUE-BND-" + UUID.randomUUID().toString().substring(0, 8);
        String patientId = "PAT-BND-" + UUID.randomUUID().toString().substring(0, 8);

        RegisterPatientRequest request = RegisterPatientRequest.builder()
                .queueId(queueId)
                .patientId(patientId)
                .patientName(nombre)
                .appointmentReference("REF-BND-" + UUID.randomUUID().toString().substring(0, 6))
                .priority(prioridad)
                .notes(notas)
                .build();

        // Act
        theActorInTheSpotlight().attemptsTo(
            RegistrarPaciente.conInfo(request)
        );
    }

    @Then("el sistema debe responder con código {int}")
    public void elSistemaDebeResponderConCodigo(int codigoEsperado) {
        // Assert
        theActorInTheSpotlight().should(
            seeThat("Código HTTP esperado", CodigoRespuesta.deLaPeticion(), equalTo(codigoEsperado))
        );
    }

    @When("se registra un paciente con datos válidos generados")
    public void seRegistraUnPacienteConDatosValidos() {
        // Arrange — TDT pattern via factory
        lastJourneyData = JourneyTestDataFactory.create(environmentVariables);
        theActorInTheSpotlight().remember(ActorMemoryKeys.JOURNEY_DATA, lastJourneyData);

        // Act
        theActorInTheSpotlight().attemptsTo(
            RegistrarPaciente.conInfo(lastJourneyData.registerPatientRequest())
        );

        // Assert
        theActorInTheSpotlight().should(
            seeThat("Registro exitoso", CodigoRespuesta.deLaPeticion(), equalTo(200))
        );
    }

    @When("se intenta registrar el mismo paciente nuevamente con los mismos identificadores")
    public void seIntentaRegistrarElMismoPacienteNuevamente() {
        // Act — registro duplicado (mismo queueId + patientId)
        theActorInTheSpotlight().attemptsTo(
            RegistrarPaciente.conInfo(lastJourneyData.registerPatientRequest())
        );
    }

    @Then("el sistema debe rechazar el duplicado con código 409 o 400")
    public void elSistemaDebeRechazarElDuplicado() {
        // Assert — unicidad: RN-01
        theActorInTheSpotlight().should(
            seeThat("Duplicado rechazado",
                CodigoRespuesta.deLaPeticion(), anyOf(equalTo(409), equalTo(400)))
        );
    }

    @When("se intenta registrar un paciente sin autenticación")
    public void seIntentaRegistrarSinAutenticacion() {
        // Arrange — actor sin token
        String baseUrl = AutomationEnvironment.apiBaseUrl(environmentVariables);
        theActorCalled("Anonimo").whoCan(CallAnApi.at(baseUrl));

        RegisterPatientRequest request = RegisterPatientRequest.builder()
                .queueId("QUEUE-ANON-" + UUID.randomUUID().toString().substring(0, 8))
                .patientId("PAT-ANON-" + UUID.randomUUID().toString().substring(0, 8))
                .patientName("Anonimo Test")
                .appointmentReference("REF-ANON")
                .priority("1")
                .notes("Test sin auth")
                .build();

        // Act — POST sin Bearer token
        theActorInTheSpotlight().attemptsTo(
            Post.to(Endpoints.RECEPTION_REGISTER)
                .with(req -> ApiRequestSupport.json(req, request))
        );
    }
}
