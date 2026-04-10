package com.sofka.utils;

import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.UUID;

public final class ApiRequestSupport {

    private static final RestAssuredConfig REST_CONFIG = RestAssuredConfig.config()
            .logConfig(LogConfig.logConfig().blacklistHeader("Authorization"));

    private ApiRequestSupport() {
    }

    public static RequestSpecification json(RequestSpecification request, Object body) {
        return baseRequest(request)
                .contentType(ContentType.JSON)
                .body(body);
    }

    public static RequestSpecification idempotentJson(RequestSpecification request, Object body) {
        return json(request, body)
                .header("X-Idempotency-Key", UUID.randomUUID().toString());
    }

    public static RequestSpecification authorizedJson(
            RequestSpecification request,
            String token,
            Object body
    ) {
        return json(request, body)
                .header("Authorization", "Bearer " + token);
    }

    public static RequestSpecification idempotentAuthorizedJson(
            RequestSpecification request,
            String token,
            Object body
    ) {
        return authorizedJson(request, token, body)
                .header("X-Idempotency-Key", UUID.randomUUID().toString());
    }

    public static RequestSpecification anonymousGet(RequestSpecification request) {
        return baseRequest(request);
    }

    public static RequestSpecification authorizedGet(
            RequestSpecification request,
            String token
    ) {
        return anonymousGet(request)
                .header("Authorization", "Bearer " + token);
    }

    private static RequestSpecification baseRequest(RequestSpecification request) {
        return request
                .config(REST_CONFIG)
                .header("Accept", "application/json")
                .header("X-Correlation-Id", UUID.randomUUID().toString());
    }
}
