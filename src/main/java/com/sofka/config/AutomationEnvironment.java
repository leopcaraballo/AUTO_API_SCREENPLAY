package com.sofka.config;

import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.model.environment.UndefinedEnvironmentVariableException;
import net.thucydides.model.util.EnvironmentVariables;

public final class AutomationEnvironment {

    private static final String DEFAULT_API_BASE_URL = "http://localhost:5094";
    private static final String DEFAULT_EXPECTED_ROLE = "Supervisor";
    private static final String DEFAULT_PATIENT_NAME = "John Doe Automado";
    private static final String DEFAULT_ROOM_NAME = "Consultorio API-Test";
    private static final String DEFAULT_CASHIER_STATION_ID = "CASH-API-01";
    private static final String DEFAULT_APPOINTMENT_PREFIX = "REF";
    private static final String DEFAULT_PAYMENT_REFERENCE_PREFIX = "PAY";
    private static final String DEFAULT_VALIDATED_AMOUNT = "35000";
    private static final String DEFAULT_PRIORITY = "1";
    private static final String DEFAULT_NOTES = "Checkup de automatizacion API";
    private static final String DEFAULT_OUTCOME = "Completed";

    private AutomationEnvironment() {
    }

    public static String apiBaseUrl(EnvironmentVariables environmentVariables) {
        return optional(environmentVariables, "restapi.baseurl", "RLAPP_API_BASE_URL", DEFAULT_API_BASE_URL);
    }

    public static Credentials validCredentials(EnvironmentVariables environmentVariables) {
        return new Credentials(
                required(environmentVariables, "credentials.valid.username", "RLAPP_VALID_USERNAME"),
                required(environmentVariables, "credentials.valid.password", "RLAPP_VALID_PASSWORD")
        );
    }

    public static Credentials supportCredentials(EnvironmentVariables environmentVariables) {
        return new Credentials(
                required(environmentVariables, "credentials.support.username", "RLAPP_SUPPORT_USERNAME"),
                required(environmentVariables, "credentials.support.password", "RLAPP_SUPPORT_PASSWORD")
        );
    }

    public static String expectedRole(EnvironmentVariables environmentVariables) {
        return optional(environmentVariables, "credentials.valid.expected.role", "RLAPP_EXPECTED_ROLE", DEFAULT_EXPECTED_ROLE);
    }

    public static String patientName(EnvironmentVariables environmentVariables) {
        return optional(environmentVariables, "testdata.patient.name", "RLAPP_API_PATIENT_NAME", DEFAULT_PATIENT_NAME);
    }

    public static String roomName(EnvironmentVariables environmentVariables) {
        return optional(environmentVariables, "testdata.room.name", "RLAPP_API_ROOM_NAME", DEFAULT_ROOM_NAME);
    }

    public static String cashierStationId(EnvironmentVariables environmentVariables) {
        return optional(environmentVariables, "testdata.cashier.station.id", "RLAPP_API_CASHIER_STATION_ID", DEFAULT_CASHIER_STATION_ID);
    }

    public static String appointmentPrefix(EnvironmentVariables environmentVariables) {
        return optional(environmentVariables, "testdata.appointment.prefix", "RLAPP_API_APPOINTMENT_PREFIX", DEFAULT_APPOINTMENT_PREFIX);
    }

    public static String paymentReferencePrefix(EnvironmentVariables environmentVariables) {
        return optional(environmentVariables, "testdata.payment.reference.prefix", "RLAPP_API_PAYMENT_REFERENCE_PREFIX", DEFAULT_PAYMENT_REFERENCE_PREFIX);
    }

    public static double validatedAmount(EnvironmentVariables environmentVariables) {
        String configuredValue = optional(environmentVariables, "testdata.payment.amount", "RLAPP_API_PAYMENT_AMOUNT", DEFAULT_VALIDATED_AMOUNT);

        try {
            return Double.parseDouble(configuredValue);
        } catch (NumberFormatException exception) {
            throw new IllegalStateException("Invalid numeric automation property: testdata.payment.amount", exception);
        }
    }

    public static String priority(EnvironmentVariables environmentVariables) {
        return optional(environmentVariables, "testdata.patient.priority", "RLAPP_API_PRIORITY", DEFAULT_PRIORITY);
    }

    public static String notes(EnvironmentVariables environmentVariables) {
        return optional(environmentVariables, "testdata.patient.notes", "RLAPP_API_NOTES", DEFAULT_NOTES);
    }

    public static String consultationOutcome(EnvironmentVariables environmentVariables) {
        return optional(environmentVariables, "testdata.consultation.outcome", "RLAPP_API_OUTCOME", DEFAULT_OUTCOME);
    }

    private static String required(
            EnvironmentVariables environmentVariables,
            String propertyName,
            String environmentVariable
    ) {
        String value = preferredValue(environmentVariables, propertyName, environmentVariable);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                "Missing required automation property: " + propertyName
                    + ". Configure system property '" + propertyName + "' or environment variable '" + environmentVariable + "'."
            );
        }

        return value;
    }

    private static String optional(
            EnvironmentVariables environmentVariables,
            String propertyName,
            String environmentVariable,
            String defaultValue
    ) {
        String value = preferredValue(environmentVariables, propertyName, environmentVariable);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private static String preferredValue(
            EnvironmentVariables environmentVariables,
            String propertyName,
            String environmentVariable
    ) {
        String systemPropertyValue = System.getProperty(propertyName);
        if (systemPropertyValue != null && !systemPropertyValue.isBlank()) {
            return systemPropertyValue;
        }

        String environmentValue = System.getenv(environmentVariable);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue;
        }

        try {
            return EnvironmentSpecificConfiguration.from(environmentVariables).getProperty(propertyName);
        } catch (UndefinedEnvironmentVariableException exception) {
            return null;
        }
    }

    public record Credentials(String username, String password) {
    }
}
