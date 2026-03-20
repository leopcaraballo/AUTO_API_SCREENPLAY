package com.sofka.models;

public class FinishConsultationRequest {
    private String turnId;
    private String queueId;
    private String patientId;
    private String consultingRoomId;
    private String outcome;

    public FinishConsultationRequest() {}

    public FinishConsultationRequest(String turnId, String queueId, String patientId, String consultingRoomId, String outcome) {
        this.turnId = turnId;
        this.queueId = queueId;
        this.patientId = patientId;
        this.consultingRoomId = consultingRoomId;
        this.outcome = outcome;
    }

    public String getTurnId() { return turnId; }
    public void setTurnId(String turnId) { this.turnId = turnId; }
    public String getQueueId() { return queueId; }
    public void setQueueId(String queueId) { this.queueId = queueId; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getConsultingRoomId() { return consultingRoomId; }
    public void setConsultingRoomId(String consultingRoomId) { this.consultingRoomId = consultingRoomId; }
    public String getOutcome() { return outcome; }
    public void setOutcome(String outcome) { this.outcome = outcome; }

    public static FinishConsultationRequestBuilder builder() { return new FinishConsultationRequestBuilder(); }

    public static class FinishConsultationRequestBuilder {
        private String turnId;
        private String queueId;
        private String patientId;
        private String consultingRoomId;
        private String outcome;
        public FinishConsultationRequestBuilder turnId(String turnId) { this.turnId = turnId; return this; }
        public FinishConsultationRequestBuilder queueId(String queueId) { this.queueId = queueId; return this; }
        public FinishConsultationRequestBuilder patientId(String patientId) { this.patientId = patientId; return this; }
        public FinishConsultationRequestBuilder consultingRoomId(String consultingRoomId) { this.consultingRoomId = consultingRoomId; return this; }
        public FinishConsultationRequestBuilder outcome(String outcome) { this.outcome = outcome; return this; }
        public FinishConsultationRequest build() { return new FinishConsultationRequest(turnId, queueId, patientId, consultingRoomId, outcome); }
    }
}
