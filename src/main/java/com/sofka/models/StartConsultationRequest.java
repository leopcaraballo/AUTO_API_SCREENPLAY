package com.sofka.models;

public class StartConsultationRequest {
    private String turnId;
    private String consultingRoomId;

    public StartConsultationRequest() {}

    public StartConsultationRequest(String turnId, String consultingRoomId) {
        this.turnId = turnId;
        this.consultingRoomId = consultingRoomId;
    }

    public String getTurnId() { return turnId; }
    public void setTurnId(String turnId) { this.turnId = turnId; }
    public String getConsultingRoomId() { return consultingRoomId; }
    public void setConsultingRoomId(String consultingRoomId) { this.consultingRoomId = consultingRoomId; }

    public static StartConsultationRequestBuilder builder() { return new StartConsultationRequestBuilder(); }

    public static class StartConsultationRequestBuilder {
        private String turnId;
        private String consultingRoomId;
        public StartConsultationRequestBuilder turnId(String turnId) { this.turnId = turnId; return this; }
        public StartConsultationRequestBuilder consultingRoomId(String consultingRoomId) { this.consultingRoomId = consultingRoomId; return this; }
        public StartConsultationRequest build() { return new StartConsultationRequest(turnId, consultingRoomId); }
    }
}
