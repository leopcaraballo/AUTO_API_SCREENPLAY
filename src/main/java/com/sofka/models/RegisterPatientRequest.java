package com.sofka.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterPatientRequest {
    private String queueId;
    private String patientId;
    private String patientName;
    private String appointmentReference;
    private String priority;
    private String notes;
}
