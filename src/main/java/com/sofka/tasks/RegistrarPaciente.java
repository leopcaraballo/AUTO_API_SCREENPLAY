package com.sofka.tasks;

import com.sofka.models.RegisterPatientRequest;
import com.sofka.utils.Endpoints;
import io.restassured.http.ContentType;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Post;
import java.util.UUID;

public class RegistrarPaciente implements Task {

    private final RegisterPatientRequest request;

    public RegistrarPaciente(RegisterPatientRequest request) {
        this.request = request;
    }

    public static RegistrarPaciente conInfo(RegisterPatientRequest request) {
        return Tasks.instrumented(RegistrarPaciente.class, request);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        String token = actor.recall("token");
        String correlationId = UUID.randomUUID().toString();

        actor.attemptsTo(
            Post.to(Endpoints.RECEPTION_REGISTER)
                .with(req -> req
                    .contentType(ContentType.JSON)
                    .header("Accept", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .header("X-Correlation-Id", correlationId)
                    .header("X-Idempotency-Key", UUID.randomUUID().toString())
                    .body(request)
                )
        );
    }
}
