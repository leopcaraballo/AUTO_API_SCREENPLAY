package com.sofka.tasks;

import com.sofka.models.ActivateRoomRequest;
import com.sofka.utils.Endpoints;
import io.restassured.http.ContentType;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Post;
import java.util.UUID;

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
        String token = actor.recall("token");
        String correlationId = UUID.randomUUID().toString();

        actor.attemptsTo(
            Post.to(Endpoints.MEDICAL_ACTIVATE_ROOM)
                .with(req -> req
                    .contentType(ContentType.JSON)
                    .header("Accept", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .header("X-Correlation-Id", correlationId)
                    .body(request)
                )
        );
    }
}
