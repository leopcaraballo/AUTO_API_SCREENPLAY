package com.sofka.tasks;

import com.sofka.models.ClaimNextRequest;
import com.sofka.utils.ActorMemoryKeys;
import com.sofka.utils.ApiRequestSupport;
import com.sofka.utils.Endpoints;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Post;

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
        String token = actor.recall(ActorMemoryKeys.AUTH_TOKEN);

        actor.attemptsTo(
            Post.to(Endpoints.MEDICAL_CALL_NEXT)
                .with(req -> ApiRequestSupport.authorizedJson(req, token, request))
        );
    }
}
