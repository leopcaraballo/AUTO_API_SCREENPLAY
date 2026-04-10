package com.sofka.tasks;

import com.sofka.models.ValidatePaymentRequest;
import com.sofka.utils.ActorMemoryKeys;
import com.sofka.utils.ApiRequestSupport;
import com.sofka.utils.Endpoints;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Post;

public class ValidarPagoPaciente implements Task {

    private final ValidatePaymentRequest request;

    public ValidarPagoPaciente(ValidatePaymentRequest request) {
        this.request = request;
    }

    public static ValidarPagoPaciente conInfo(ValidatePaymentRequest request) {
        return Tasks.instrumented(ValidarPagoPaciente.class, request);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        String token = actor.recall(ActorMemoryKeys.AUTH_TOKEN);

        actor.attemptsTo(
            Post.to(Endpoints.CASHIER_VALIDATE_PAYMENT)
                .with(req -> ApiRequestSupport.authorizedJson(req, token, request))
        );
    }
}
