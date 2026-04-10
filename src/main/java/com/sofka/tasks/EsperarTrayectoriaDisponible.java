package com.sofka.tasks;

import com.sofka.utils.ActorMemoryKeys;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;

import java.time.Duration;

import static org.awaitility.Awaitility.await;

public class EsperarTrayectoriaDisponible implements Task {

    private static final long DEFAULT_POLL_INTERVAL_MILLIS = 500L;
    private static final long DEFAULT_TIMEOUT_SECONDS = 20L;

    private final String patientId;
    private final String queueId;
    private final int minimumResults;

    public EsperarTrayectoriaDisponible(String patientId, String queueId, int minimumResults) {
        this.patientId = patientId;
        this.queueId = queueId;
        this.minimumResults = minimumResults;
    }

    public static EsperarTrayectoriaDisponible delPaciente(String patientId, String queueId) {
        return Tasks.instrumented(EsperarTrayectoriaDisponible.class, patientId, queueId, 1);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        await()
                .pollDelay(Duration.ZERO)
            .pollInterval(Duration.ofMillis(resolveLong(
                "automation.trajectory.poll.interval.millis",
                "RLAPP_API_TRAJECTORY_POLL_INTERVAL_MILLIS",
                DEFAULT_POLL_INTERVAL_MILLIS
            )))
            .atMost(Duration.ofSeconds(resolveLong(
                "automation.trajectory.poll.timeout.seconds",
                "RLAPP_API_TRAJECTORY_POLL_TIMEOUT_SECONDS",
                DEFAULT_TIMEOUT_SECONDS
            )))
                .ignoreExceptions()
                .pollInSameThread()
                .until(() -> {
                    actor.attemptsTo(BuscarTrayectorias.delPaciente(patientId, queueId));
                    return SerenityRest.lastResponse() != null
                            && SerenityRest.lastResponse().statusCode() == 200
                            && SerenityRest.lastResponse().jsonPath().getInt("total") >= minimumResults;
                });

        // Re-fetch on main thread to guarantee SerenityRest.lastResponse() is set
        actor.attemptsTo(BuscarTrayectorias.delPaciente(patientId, queueId));

        Integer total = SerenityRest.lastResponse().jsonPath().getInt("total");
        actor.remember(ActorMemoryKeys.DISCOVERY_TOTAL, total);

        String trajectoryId = SerenityRest.lastResponse().jsonPath().getString("items[0].trajectoryId");
        if (trajectoryId != null && !trajectoryId.isBlank()) {
            actor.remember(ActorMemoryKeys.TRAJECTORY_ID, trajectoryId);
        }
    }

    private long resolveLong(String propertyName, String environmentVariable, long defaultValue) {
        String configuredValue = System.getProperty(propertyName);
        if (configuredValue == null || configuredValue.isBlank()) {
            configuredValue = System.getenv(environmentVariable);
        }

        if (configuredValue == null || configuredValue.isBlank()) {
            return defaultValue;
        }

        try {
            return Long.parseLong(configuredValue);
        } catch (NumberFormatException exception) {
            throw new IllegalStateException("Invalid numeric automation property: " + propertyName, exception);
        }
    }
}
