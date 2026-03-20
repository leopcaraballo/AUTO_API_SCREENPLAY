package com.sofka.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FinishConsultationRequest {
    private String turnId;
    private String queueId;
    private String patientId;
    private String consultingRoomId;
    private String outcome;
}
