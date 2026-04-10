package com.sofka.stepdefinitions;

import com.sofka.config.AutomationEnvironment;
import com.sofka.config.AutomationEnvironment.Credentials;
import com.sofka.models.RebuildTrajectoriesRequest;
import com.sofka.questions.CodigoRespuesta;
import com.sofka.questions.ResponseField;
import com.sofka.questions.ResponseListSize;
import com.sofka.tasks.*;
import com.sofka.utils.ActorMemoryKeys;
import com.sofka.utils.JourneyTestDataFactory.JourneyData;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.thucydides.model.util.EnvironmentVariables;

import java.util.List;
import java.util.UUID;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.hamcrest.Matchers.*;

public class TrayectoriaPacienteStepDefinitions {

    private EnvironmentVariables environmentVariables;

    @And("se espera la propagación de eventos de trayectoria")
    public void seEsperaLaPropagacionDeEventos() {
        JourneyData data = currentJourneyData();

        theActorInTheSpotlight().attemptsTo(
            EsperarTrayectoriaDisponible.delPaciente(data.patientId(), data.queueId())
        );
    }

    @Then("el sistema debe retornar al menos una trayectoria para el paciente atendido")
    public void elSistemaDebeRetornarAlMenosUnaTrayectoria() {
        assertSuccessfulDiscovery();
    }

    @And("la trayectoria debe contener las etapas del recorrido clínico completo")
    public void laTrayectoriaDebeContenerLasEtapas() {
        String trajectoryId = rememberedTrajectoryId();

        theActorInTheSpotlight().attemptsTo(
            ConsultarTrayectoria.conId(trajectoryId)
        );
        theActorInTheSpotlight().should(
            seeThat("Código al consultar trayectoria", CodigoRespuesta.deLaPeticion(), equalTo(200))
        );
        theActorInTheSpotlight().should(
            seeThat("El trajectoryId coincide", ResponseField.<String>valueOf("trajectoryId"), equalTo(trajectoryId))
        );
        theActorInTheSpotlight().should(
            seeThat("Las etapas registradas son al menos 3", ResponseListSize.of("stages"), greaterThanOrEqualTo(3))
        );

        List<String> stages = SerenityRest.lastResponse().jsonPath().getList("stages.stage");
        theActorInTheSpotlight().should(
            seeThat("Contiene etapa Recepcion",
                actor -> stages.stream().anyMatch(s -> s.contains("Recepcion")), is(true))
        );
    }

    @And("la trayectoria debe estar en estado finalizado")
    public void laTrayectoriaDebeEstarFinalizada() {
        theActorInTheSpotlight().should(
            seeThat("El estado actual es finalizado",
                ResponseField.<String>valueOf("currentState"), containsString("Finalizada"))
        );
    }

    @And("se descubre la trayectoria del paciente por su identificador")
    public void seDescubreLaTrayectoriaPorId() {
        JourneyData data = currentJourneyData();

        theActorInTheSpotlight().attemptsTo(
            EsperarTrayectoriaDisponible.delPaciente(data.patientId(), data.queueId())
        );
        assertSuccessfulDiscovery();
    }

    @Then("el detalle de la trayectoria debe incluir las etapas registradas")
    public void elDetalleDebeIncluirEtapas() {
        String trajectoryId = rememberedTrajectoryId();

        theActorInTheSpotlight().attemptsTo(
            ConsultarTrayectoria.conId(trajectoryId)
        );
        theActorInTheSpotlight().should(
            seeThat("Código al consultar detalle", CodigoRespuesta.deLaPeticion(), equalTo(200))
        );
        theActorInTheSpotlight().should(
            seeThat("El patientId del detalle", ResponseField.<String>valueOf("patientId"),
                equalTo(currentJourneyData().patientId()))
        );
        theActorInTheSpotlight().should(
            seeThat("Las etapas existen", ResponseListSize.of("stages"), greaterThanOrEqualTo(1))
        );
    }

    @And("cada etapa debe contener timestamp y evento fuente")
    public void cadaEtapaDebeContenerTimestampYEvento() {
        List<String> occurredAts = SerenityRest.lastResponse().jsonPath().getList("stages.occurredAt");
        List<String> sourceEvents = SerenityRest.lastResponse().jsonPath().getList("stages.sourceEvent");

        theActorInTheSpotlight().should(
            seeThat("Todas las etapas tienen timestamp",
                actor -> occurredAts.stream().allMatch(t -> t != null && !t.isBlank()), is(true))
        );
        theActorInTheSpotlight().should(
            seeThat("Todas las etapas tienen evento fuente",
                actor -> sourceEvents.stream().allMatch(e -> e != null && !e.isBlank()), is(true))
        );
    }

    @When("se busca la trayectoria de un paciente que no existe en el sistema")
    public void seBuscaTrayectoriaDePacienteInexistente() {
        String fakePatientId = "NONEXISTENT-" + UUID.randomUUID().toString().substring(0, 8);
        theActorInTheSpotlight().attemptsTo(
            BuscarTrayectorias.delPaciente(fakePatientId)
        );
    }

    @Then("el sistema debe retornar cero trayectorias encontradas")
    public void elSistemaDebeRetornarCeroTrayectorias() {
        theActorInTheSpotlight().should(
            seeThat("Código al buscar inexistente", CodigoRespuesta.deLaPeticion(), equalTo(200))
        );
        theActorInTheSpotlight().should(
            seeThat("Total de trayectorias es cero", ResponseField.<Integer>valueOf("total"), equalTo(0))
        );
        theActorInTheSpotlight().should(
            seeThat("La lista de items está vacía", ResponseListSize.of("items"), equalTo(0))
        );
    }

    @And("se solicita la reconstrucción de trayectorias en modo simulación")
    public void seSolicitaReconstruccionEnSimulacion() {
        JourneyData data = currentJourneyData();

        Credentials supportCreds = AutomationEnvironment.supportCredentials(environmentVariables);
        String baseUrl = AutomationEnvironment.apiBaseUrl(environmentVariables);

        theActorCalled("SupportAgent").whoCan(CallAnApi.at(baseUrl));
        theActorInTheSpotlight().attemptsTo(
            AutenticarStaff.con(supportCreds.username(), supportCreds.password())
        );

        RebuildTrajectoriesRequest request = RebuildTrajectoriesRequest.builder()
                .queueId(data.queueId())
                .patientId(data.patientId())
                .dryRun(true)
                .build();

        theActorInTheSpotlight().attemptsTo(
            ReconstruirTrayectorias.conInfo(request)
        );
    }

    @Then("el resultado de la reconstrucción debe indicar modo simulación")
    public void elResultadoDebeIndicarModoSimulacion() {
        theActorInTheSpotlight().should(
            seeThat("Código al reconstruir", CodigoRespuesta.deLaPeticion(), equalTo(200))
        );
        theActorInTheSpotlight().should(
            seeThat("El modo es dry run", ResponseField.<Boolean>valueOf("dryRun"), is(true))
        );
        theActorInTheSpotlight().should(
            seeThat("El jobId existe", ResponseField.<String>valueOf("jobId"), notNullValue())
        );
    }

    @And("el resultado debe reportar eventos procesados")
    public void elResultadoDebeReportarEventos() {
        theActorInTheSpotlight().should(
            seeThat("Eventos procesados", ResponseField.<Integer>valueOf("eventsProcessed"), greaterThanOrEqualTo(0))
        );
        theActorInTheSpotlight().should(
            seeThat("Trayectorias procesadas", ResponseField.<Integer>valueOf("trajectoriesProcessed"), greaterThanOrEqualTo(0))
        );
    }

    @When("se consulta el discovery de trayectorias sin autenticación")
    public void seConsultaElDiscoverySinAutenticacion() {
        stageApiActor("InvitadoDiscovery");
        theActorInTheSpotlight().attemptsTo(
            BuscarTrayectorias.sinAutenticacion("NONEXISTENT-" + UUID.randomUUID().toString().substring(0, 8))
        );
    }

    @When("se consulta el detalle de una trayectoria sin autenticación")
    public void seConsultaElDetalleSinAutenticacion() {
        stageApiActor("InvitadoDetalle");
        theActorInTheSpotlight().attemptsTo(
            ConsultarTrayectoria.sinAutenticacion("TRJ-" + UUID.randomUUID().toString().replace("-", ""))
        );
    }

    @When("se intenta reconstruir una trayectoria sin autenticación")
    public void seIntentaReconstruirSinAutenticacion() {
        stageApiActor("InvitadoRebuild");
        theActorInTheSpotlight().attemptsTo(
            ReconstruirTrayectorias.sinAutenticacion(fakeRebuildRequest())
        );
    }

    @When("un supervisor intenta reconstruir trayectorias sin privilegios de soporte")
    public void unSupervisorIntentaReconstruirSinPrivilegiosDeSoporte() {
        theActorInTheSpotlight().attemptsTo(
            ReconstruirTrayectorias.conInfo(fakeRebuildRequest())
        );
    }

    @Then("el sistema debe rechazar la solicitud con código {int}")
    public void elSistemaDebeRechazarLaSolicitudConCodigo(int statusCode) {
        theActorInTheSpotlight().should(
            seeThat("Código de rechazo esperado", CodigoRespuesta.deLaPeticion(), equalTo(statusCode))
        );
    }

    private JourneyData currentJourneyData() {
        return theActorInTheSpotlight().recall(ActorMemoryKeys.JOURNEY_DATA);
    }

    private void assertSuccessfulDiscovery() {
        theActorInTheSpotlight().should(
            seeThat("Código al descubrir trayectorias", CodigoRespuesta.deLaPeticion(), equalTo(200))
        );
        theActorInTheSpotlight().should(
            seeThat("Total de trayectorias encontradas", ResponseField.<Integer>valueOf("total"), greaterThanOrEqualTo(1))
        );
        theActorInTheSpotlight().should(
            seeThat("La lista de items tiene al menos 1", ResponseListSize.of("items"), greaterThanOrEqualTo(1))
        );

        String trajectoryId = SerenityRest.lastResponse().jsonPath().getString("items[0].trajectoryId");
        theActorInTheSpotlight().remember(ActorMemoryKeys.TRAJECTORY_ID, trajectoryId);
    }

    private String rememberedTrajectoryId() {
        String trajectoryId = theActorInTheSpotlight().recall(ActorMemoryKeys.TRAJECTORY_ID);

        if (trajectoryId == null || trajectoryId.isBlank()) {
            throw new IllegalStateException("trajectoryId not available; discovery may have failed.");
        }

        return trajectoryId;
    }

    private void stageApiActor(String actorName) {
        String baseUrl = AutomationEnvironment.apiBaseUrl(environmentVariables);
        theActorCalled(actorName).whoCan(CallAnApi.at(baseUrl));
    }

    private RebuildTrajectoriesRequest fakeRebuildRequest() {
        String suffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        return RebuildTrajectoriesRequest.builder()
                .queueId("QUEUE-" + suffix)
                .patientId("PATIENT-" + suffix)
                .dryRun(true)
                .build();
    }
}
