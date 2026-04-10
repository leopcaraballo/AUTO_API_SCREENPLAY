package com.sofka.tasks;

import com.sofka.models.ActivateRoomRequest;
import com.sofka.utils.ActorMemoryKeys;
import com.sofka.utils.ApiRequestSupport;
import com.sofka.utils.Endpoints;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Post;

public class ActivarConsultorio implements Task {

    private final ActivateRoomRequest request;

    public ActivarConsultorio(ActivateRoomRequest request) {
        this.request = request;
    }

    public static ActivarConsultorio conInfo(ActivateRoomRequest request) {
        return Tasks.instrumented(ActivarConsultorio.class, request);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        String token = actor.recall(ActorMemoryKeys.AUTH_TOKEN);

        actor.attemptsTo(
            Post.to(Endpoints.MEDICAL_ACTIVATE_ROOM)
                .with(req -> ApiRequestSupport.authorizedJson(req, token, request))
        );
    }
}
