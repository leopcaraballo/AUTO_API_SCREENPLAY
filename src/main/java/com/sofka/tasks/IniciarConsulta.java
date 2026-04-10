package com.sofka.tasks;

import com.sofka.models.StartConsultationRequest;
import com.sofka.utils.ActorMemoryKeys;
import com.sofka.utils.ApiRequestSupport;
import com.sofka.utils.Endpoints;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Post;

public class IniciarConsulta implements Task {

    private final StartConsultationRequest request;

    public IniciarConsulta(StartConsultationRequest request) {
        this.request = request;
    }

    public static IniciarConsulta conInfo(StartConsultationRequest request) {
        return Tasks.instrumented(IniciarConsulta.class, request);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        String token = actor.recall(ActorMemoryKeys.AUTH_TOKEN);

        actor.attemptsTo(
            Post.to(Endpoints.MEDICAL_START_CONSULTATION)
                .with(req -> ApiRequestSupport.authorizedJson(req, token, request))
        );
    }
}
