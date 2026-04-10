package com.sofka.tasks;

import com.sofka.models.RebuildTrajectoriesRequest;
import com.sofka.utils.ActorMemoryKeys;
import com.sofka.utils.ApiRequestSupport;
import com.sofka.utils.Endpoints;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Post;

public class ReconstruirTrayectorias implements Task {

    private final RebuildTrajectoriesRequest request;
    private final boolean authenticated;

    public ReconstruirTrayectorias(RebuildTrajectoriesRequest request, boolean authenticated) {
        this.request = request;
        this.authenticated = authenticated;
    }

    public static ReconstruirTrayectorias conInfo(RebuildTrajectoriesRequest request) {
        return Tasks.instrumented(ReconstruirTrayectorias.class, request, true);
    }

    public static ReconstruirTrayectorias sinAutenticacion(RebuildTrajectoriesRequest request) {
        return Tasks.instrumented(ReconstruirTrayectorias.class, request, false);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        if (authenticated) {
            String token = actor.recall(ActorMemoryKeys.AUTH_TOKEN);
            actor.attemptsTo(
                Post.to(Endpoints.TRAJECTORY_REBUILD)
                    .with(req -> ApiRequestSupport.idempotentAuthorizedJson(req, token, request))
            );
            return;
        }

        actor.attemptsTo(
            Post.to(Endpoints.TRAJECTORY_REBUILD)
                .with(req -> ApiRequestSupport.idempotentJson(req, request))
        );
    }
}
