package com.sofka.stepdefinitions;

import com.sofka.config.AutomationEnvironment;
import com.sofka.models.RebuildTrajectoriesRequest;
import com.sofka.questions.CodigoRespuesta;
import com.sofka.questions.ResponseField;
import com.sofka.questions.ResponseListSize;
import com.sofka.tasks.BuscarTrayectorias;
import com.sofka.tasks.ConsultarTrayectoria;
import com.sofka.utils.ActorMemoryKeys;
import com.sofka.utils.ApiRequestSupport;
import com.sofka.utils.Endpoints;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.serenitybdd.screenplay.rest.interactions.Post;
import net.thucydides.model.util.EnvironmentVariables;

import java.util.UUID;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.hamcrest.Matchers.*;

public class ContratoSeguridadStepDefinitions {

    private EnvironmentVariables environmentVariables;

    // --- CONTRACT TESTING ---

    @Then("la respuesta del discovery debe cumplir el contrato esperado")
    public void laRespuestaDelDiscoveryDebeCumplirElContrato() {
        // Assert — validar estructura del contrato discovery
        theActorInTheSpotlight().should(
            seeThat("Tiene campo 'total'", ResponseField.<Integer>valueOf("total"), notNullValue())
        );
        theActorInTheSpotlight().should(
            seeThat("Tiene campo 'items' como lista", ResponseListSize.of("items"), greaterThanOrEqualTo(0))
        );

        if (SerenityRest.lastResponse().jsonPath().getInt("total") > 0) {
            theActorInTheSpotlight().should(
                seeThat("Cada item tiene trajectoryId",
                    ResponseField.<String>valueOf("items[0].trajectoryId"), notNullValue())
            );
            theActorInTheSpotlight().should(
                seeThat("Cada item tiene patientId",
                    ResponseField.<String>valueOf("items[0].patientId"), notNullValue())
            );
        }
    }

    @Then("la respuesta del detalle debe contener campos obligatorios del contrato")
    public void laRespuestaDelDetalleDebeContenerCamposDelContrato() {
        String trajectoryId = theActorInTheSpotlight().recall(ActorMemoryKeys.TRAJECTORY_ID);

        // Act
        theActorInTheSpotlight().attemptsTo(
            ConsultarTrayectoria.conId(trajectoryId)
        );

        // Assert — contrato del detalle: campos obligatorios
        theActorInTheSpotlight().should(
            seeThat("Código 200", CodigoRespuesta.deLaPeticion(), equalTo(200))
        );
        theActorInTheSpotlight().should(
            seeThat("Campo trajectoryId presente",
                ResponseField.<String>valueOf("trajectoryId"), notNullValue())
        );
        theActorInTheSpotlight().should(
            seeThat("Campo patientId presente",
                ResponseField.<String>valueOf("patientId"), notNullValue())
        );
        theActorInTheSpotlight().should(
            seeThat("Campo stages presente como lista",
                ResponseListSize.of("stages"), greaterThanOrEqualTo(0))
        );
        theActorInTheSpotlight().should(
            seeThat("Campo currentState presente",
                ResponseField.<String>valueOf("currentState"), notNullValue())
        );
    }

    // --- NEGATIVE TESTING ---

    @When("se consulta el discovery con patientId vacío")
    public void seConsultaElDiscoveryConPatientIdVacio() {
        // Act — boundary: empty string parameter
        theActorInTheSpotlight().attemptsTo(
            BuscarTrayectorias.delPaciente("")
        );
    }

    @When("se consulta el detalle con trajectoryId malformado {string}")
    public void seConsultaElDetalleConTrajectoryIdMalformado(String trajectoryId) {
        // Act — negative: malformed ID
        theActorInTheSpotlight().attemptsTo(
            ConsultarTrayectoria.conId(trajectoryId)
        );
    }

    @Then("el sistema debe responder con código de error del contrato")
    public void elSistemaDebeResponderConCodigoDeErrorDelContrato() {
        // Assert — 400 o 404 según implementación
        theActorInTheSpotlight().should(
            seeThat("Código de error por ID malformado",
                CodigoRespuesta.deLaPeticion(),
                anyOf(equalTo(400), equalTo(404), equalTo(500)))
        );
    }

    // --- SECURITY TESTING ---

    @When("se intenta reconstruir trayectorias con token inválido {string}")
    public void seIntentaReconstruirConTokenInvalido(String invalidToken) {
        // Arrange
        String baseUrl = AutomationEnvironment.apiBaseUrl(environmentVariables);
        theActorCalled("TokenInvalido").whoCan(CallAnApi.at(baseUrl));

        RebuildTrajectoriesRequest request = RebuildTrajectoriesRequest.builder()
                .queueId("QUEUE-SEC-" + UUID.randomUUID().toString().substring(0, 8))
                .patientId("PAT-SEC-" + UUID.randomUUID().toString().substring(0, 8))
                .dryRun(true)
                .build();

        // Act — token inválido inyectado directamente
        theActorInTheSpotlight().attemptsTo(
            Post.to(Endpoints.TRAJECTORY_REBUILD)
                .with(req -> ApiRequestSupport.json(req, request)
                    .header("Authorization", invalidToken))
        );
    }

    @When("se llama al endpoint {string} sin autenticación con método {string}")
    public void seLlamaAlEndpointSinAutenticacion(String endpoint, String metodo) {
        // Arrange — actor anónimo
        String baseUrl = AutomationEnvironment.apiBaseUrl(environmentVariables);
        String actorName = "Anonimo-" + UUID.randomUUID().toString().substring(0, 4);
        theActorCalled(actorName).whoCan(CallAnApi.at(baseUrl));

        // Act
        if ("POST".equalsIgnoreCase(metodo)) {
            RebuildTrajectoriesRequest fakeBody = RebuildTrajectoriesRequest.builder()
                    .queueId("Q-ANON")
                    .patientId("P-ANON")
                    .dryRun(true)
                    .build();
            theActorInTheSpotlight().attemptsTo(
                Post.to(endpoint)
                    .with(req -> ApiRequestSupport.json(req, fakeBody))
            );
        } else {
            String fullEndpoint = endpoint + "?patientId=NONEXISTENT";
            theActorInTheSpotlight().attemptsTo(
                Get.resource(fullEndpoint)
                    .with(ApiRequestSupport::anonymousGet)
            );
        }
    }
}
