package com.sofka.tasks;

import com.sofka.models.FinishConsultationRequest;
import com.sofka.utils.Endpoints;
import io.restassured.http.ContentType;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Post;
import java.util.UUID;

public class FinalizarConsulta implements Task {

    private final FinishConsultationRequest request;

    public FinalizarConsulta(FinishConsultationRequest request) {
        this.request = request;
    }

    public static FinalizarConsulta conInfo(FinishConsultationRequest request) {
        return Tasks.instrumented(FinalizarConsulta.class, request);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        String token = actor.recall("token");
        String correlationId = UUID.randomUUID().toString();

        actor.attemptsTo(
            Post.to(Endpoints.MEDICAL_FINISH)
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
