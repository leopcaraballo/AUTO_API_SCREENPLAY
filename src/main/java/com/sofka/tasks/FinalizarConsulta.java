package com.sofka.tasks;

import com.sofka.models.FinishConsultationRequest;
import com.sofka.utils.ActorMemoryKeys;
import com.sofka.utils.ApiRequestSupport;
import com.sofka.utils.Endpoints;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Post;

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
        String token = actor.recall(ActorMemoryKeys.AUTH_TOKEN);

        actor.attemptsTo(
            Post.to(Endpoints.MEDICAL_FINISH)
                .with(req -> ApiRequestSupport.authorizedJson(req, token, request))
        );
    }
}
