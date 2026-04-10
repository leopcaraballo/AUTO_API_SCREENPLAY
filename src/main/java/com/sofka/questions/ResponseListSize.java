package com.sofka.questions;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

import java.util.List;

public class ResponseListSize implements Question<Integer> {

    private final String listPath;

    private ResponseListSize(String listPath) {
        this.listPath = listPath;
    }

    public static ResponseListSize of(String listPath) {
        return new ResponseListSize(listPath);
    }

    @Override
    public Integer answeredBy(Actor actor) {
        List<?> list = SerenityRest.lastResponse().jsonPath().getList(listPath);
        return list != null ? list.size() : 0;
    }
}
