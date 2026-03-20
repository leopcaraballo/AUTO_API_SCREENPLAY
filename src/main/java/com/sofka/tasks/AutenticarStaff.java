package com.sofka.tasks;

import com.sofka.models.LoginRequest;
import com.sofka.utils.Endpoints;
import io.restassured.http.ContentType;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Post;

public class AutenticarStaff implements Task {

    private final String username;
    private final String password;

    public AutenticarStaff(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static AutenticarStaff con(String username, String password) {
        return Tasks.instrumented(AutenticarStaff.class, username, password);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        LoginRequest login = LoginRequest.builder()
                .identifier(username)
                .password(password)
                .build();

        actor.attemptsTo(
            Post.to(Endpoints.AUTH_LOGIN)
                .with(request -> request
                    .contentType(ContentType.JSON)
                    .header("Accept", "application/json")
                    .body(login)
                )
        );

        String token = net.serenitybdd.rest.SerenityRest.lastResponse().path("accessToken");
        if (token != null) {
            actor.remember("token", token);
        }
    }
}
