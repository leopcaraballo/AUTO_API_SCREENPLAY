package com.sofka.tasks;

import com.sofka.utils.ActorMemoryKeys;
import com.sofka.utils.ApiRequestSupport;
import com.sofka.utils.Endpoints;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Get;

public class ConsultarTrayectoria implements Task {

    private final String trajectoryId;
    private final boolean authenticated;

    public ConsultarTrayectoria(String trajectoryId, boolean authenticated) {
        this.trajectoryId = trajectoryId;
        this.authenticated = authenticated;
    }

    public static ConsultarTrayectoria conId(String trajectoryId) {
        return Tasks.instrumented(ConsultarTrayectoria.class, trajectoryId, true);
    }

    public static ConsultarTrayectoria sinAutenticacion(String trajectoryId) {
        return Tasks.instrumented(ConsultarTrayectoria.class, trajectoryId, false);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        String endpoint = Endpoints.TRAJECTORY_GET_BY_ID.replace("{trajectoryId}", trajectoryId);

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
