package com.sofka.utils;

import com.sofka.config.AutomationEnvironment;
import com.sofka.models.ActivateRoomRequest;
import com.sofka.models.CallNextAtCashierRequest;
import com.sofka.models.ClaimNextRequest;
import com.sofka.models.FinishConsultationRequest;
import com.sofka.models.RegisterPatientRequest;
import com.sofka.models.StartConsultationRequest;
import com.sofka.models.ValidatePaymentRequest;
import net.thucydides.model.util.EnvironmentVariables;

import java.util.UUID;

public final class JourneyTestDataFactory {

    private JourneyTestDataFactory() {
    }

    public static JourneyData create(EnvironmentVariables environmentVariables) {
        String queueId = generatedId("QUEUE");
        String patientId = generatedId("PATIENT");
        String roomId = generatedId("ROOM");

        return new JourneyData(
                queueId,
                patientId,
                AutomationEnvironment.patientName(environmentVariables),
                AutomationEnvironment.appointmentPrefix(environmentVariables) + "-" + UUID.randomUUID().toString().substring(0, 6),
                AutomationEnvironment.priority(environmentVariables),
                AutomationEnvironment.notes(environmentVariables),
                roomId,
                AutomationEnvironment.roomName(environmentVariables),
                AutomationEnvironment.cashierStationId(environmentVariables),
                AutomationEnvironment.paymentReferencePrefix(environmentVariables) + "-" + UUID.randomUUID().toString().substring(0, 6),
                AutomationEnvironment.validatedAmount(environmentVariables),
                AutomationEnvironment.consultationOutcome(environmentVariables)
        );
    }

    private static String generatedId(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Boundary: empty patient name — should be rejected by API (RN-12).
     */
    public static RegisterPatientRequest emptyNameRequest() {
        return RegisterPatientRequest.builder()
                .queueId(generatedId("QUEUE"))
                .patientId(generatedId("PATIENT"))
                .patientName("")
                .appointmentReference("REF-EDGE-" + UUID.randomUUID().toString().substring(0, 6))
                .priority("1")
                .notes("Edge case: empty name")
                .build();
    }

    /**
     * Boundary: name exceeding 255 characters — should be rejected (RN-12).
     */
    public static RegisterPatientRequest overflowNameRequest() {
        String longName = "A".repeat(300);
        return RegisterPatientRequest.builder()
                .queueId(generatedId("QUEUE"))
                .patientId(generatedId("PATIENT"))
                .patientName(longName)
                .appointmentReference("REF-OVF-" + UUID.randomUUID().toString().substring(0, 6))
                .priority("1")
                .notes("Edge case: overflow name")
                .build();
    }

    /**
     * Equivalence partitioning: negative priority — should be rejected (RN-14).
     */
    public static RegisterPatientRequest negativePriorityRequest() {
        return RegisterPatientRequest.builder()
                .queueId(generatedId("QUEUE"))
                .patientId(generatedId("PATIENT"))
                .patientName("Negative Priority Patient")
                .appointmentReference("REF-NEG-" + UUID.randomUUID().toString().substring(0, 6))
                .priority("-1")
                .notes("Equivalence: negative priority")
                .build();
    }

    public record JourneyData(
            String queueId,
            String patientId,
            String patientName,
            String appointmentReference,
            String priority,
            String notes,
            String roomId,
            String roomName,
                String cashierStationId,
                String paymentReference,
                double validatedAmount,
            String outcome
    ) {

        public RegisterPatientRequest registerPatientRequest() {
            return RegisterPatientRequest.builder()
                    .queueId(queueId)
                    .patientId(patientId)
                    .patientName(patientName)
                    .appointmentReference(appointmentReference)
                    .priority(priority)
                    .notes(notes)
                    .build();
        }

        public ActivateRoomRequest activateRoomRequest() {
            return ActivateRoomRequest.builder()
                    .roomId(roomId)
                    .roomName(roomName)
                    .build();
        }

        public CallNextAtCashierRequest callNextAtCashierRequest() {
            return CallNextAtCashierRequest.builder()
                    .queueId(queueId)
                    .cashierStationId(cashierStationId)
                    .build();
        }

        public ValidatePaymentRequest validatePaymentRequest(String turnId) {
            return ValidatePaymentRequest.builder()
                    .turnId(turnId)
                    .queueId(queueId)
                    .patientId(patientId)
                    .paymentReference(paymentReference)
                    .validatedAmount(validatedAmount)
                    .build();
        }

        public ClaimNextRequest medicalCallNextRequest() {
            return ClaimNextRequest.builder()
                    .queueId(queueId)
                    .consultingRoomId(roomId)
                    .build();
        }

        public StartConsultationRequest startConsultationRequest(String turnId) {
            return StartConsultationRequest.builder()
                    .turnId(turnId)
                    .consultingRoomId(roomId)
                    .build();
        }

        public FinishConsultationRequest finishConsultationRequest(String turnId) {
            return FinishConsultationRequest.builder()
                    .turnId(turnId)
                    .queueId(queueId)
                    .patientId(patientId)
                    .consultingRoomId(roomId)
                    .outcome(outcome)
                    .build();
        }
    }
}
