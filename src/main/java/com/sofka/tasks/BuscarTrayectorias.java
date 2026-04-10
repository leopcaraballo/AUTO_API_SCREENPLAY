package com.sofka.tasks;

import com.sofka.utils.ActorMemoryKeys;
import com.sofka.utils.ApiRequestSupport;
import com.sofka.utils.Endpoints;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Get;

public class BuscarTrayectorias implements Task {

    private final String patientId;
    private final String queueId;
    private final boolean authenticated;

    public BuscarTrayectorias(String patientId, String queueId, boolean authenticated) {
        this.patientId = patientId;
        this.queueId = queueId;
        this.authenticated = authenticated;
    }

    public static BuscarTrayectorias delPaciente(String patientId, String queueId) {
        return Tasks.instrumented(BuscarTrayectorias.class, patientId, queueId, true);
    }

    public static BuscarTrayectorias delPaciente(String patientId) {
        return Tasks.instrumented(BuscarTrayectorias.class, patientId, (String) null, true);
    }

    public static BuscarTrayectorias sinAutenticacion(String patientId) {
        return Tasks.instrumented(BuscarTrayectorias.class, patientId, (String) null, false);
    }

    public static BuscarTrayectorias sinAutenticacion(String patientId, String queueId) {
        return Tasks.instrumented(BuscarTrayectorias.class, patientId, queueId, false);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        String url = Endpoints.TRAJECTORY_DISCOVER + "?patientId=" + patientId;
        if (queueId != null && !queueId.isBlank()) {
            url += "&queueId=" + queueId;
        }

        final String endpoint = url;
        if (authenticated) {
            String token = actor.recall(ActorMemoryKeys.AUTH_TOKEN);
            actor.attemptsTo(
                Get.resource(endpoint)
                    .with(req -> ApiRequestSupport.authorizedGet(req, token))
            );
            return;
        }

        actor.attemptsTo(
            Get.resource(endpoint)
                .with(ApiRequestSupport::anonymousGet)
        );
    }
}
