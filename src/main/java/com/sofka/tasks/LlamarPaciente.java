package com.sofka.tasks;

import com.sofka.models.ClaimNextRequest;
import com.sofka.utils.Endpoints;
import io.restassured.http.ContentType;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Post;
import java.util.UUID;

public class LlamarPaciente implements Task {

    private final ClaimNextRequest request;

    public LlamarPaciente(ClaimNextRequest request) {
        this.request = request;
    }

    public static LlamarPaciente conInfo(ClaimNextRequest request) {
        return Tasks.instrumented(LlamarPaciente.class, request);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        String token = actor.recall("token");
        String correlationId = UUID.randomUUID().toString();

        actor.attemptsTo(
            Post.to(Endpoints.WAITING_ROOM_CLAIM)
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
