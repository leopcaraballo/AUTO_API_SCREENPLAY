package com.sofka.questions;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class ResponseField<T> implements Question<T> {

    private final String fieldPath;

    private ResponseField(String fieldPath) {
        this.fieldPath = fieldPath;
    }

    public static <T> ResponseField<T> valueOf(String fieldPath) {
        return new ResponseField<>(fieldPath);
    }

    @Override
    public T answeredBy(Actor actor) {
        return SerenityRest.lastResponse().path(fieldPath);
    }
}
