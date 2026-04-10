package com.sofka.tasks;

import com.sofka.models.CallNextAtCashierRequest;
import com.sofka.utils.ActorMemoryKeys;
import com.sofka.utils.ApiRequestSupport;
import com.sofka.utils.Endpoints;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Post;

public class LlamarPacienteEnCaja implements Task {

    private final CallNextAtCashierRequest request;

    public LlamarPacienteEnCaja(CallNextAtCashierRequest request) {
        this.request = request;
    }

    public static LlamarPacienteEnCaja conInfo(CallNextAtCashierRequest request) {
        return Tasks.instrumented(LlamarPacienteEnCaja.class, request);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        String token = actor.recall(ActorMemoryKeys.AUTH_TOKEN);

        actor.attemptsTo(
            Post.to(Endpoints.CASHIER_CALL_NEXT)
                .with(req -> ApiRequestSupport.idempotentAuthorizedJson(req, token, request))
        );
    }
}
