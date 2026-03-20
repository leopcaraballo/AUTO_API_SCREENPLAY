package com.sofka.questions;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class RecursoConsultado implements Question<String> {

    public static RecursoConsultado nombre() {
        return new RecursoConsultado();
    }

    @Override
    public String answeredBy(Actor actor) {
        return SerenityRest.lastResponse().path("firstname");
    }
}
