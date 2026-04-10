package com.sofka.models;

public class ClaimNextRequest {
    private String queueId;
    private String consultingRoomId;

    public ClaimNextRequest() {}

    public ClaimNextRequest(String queueId, String consultingRoomId) {
        this.queueId = queueId;
        this.consultingRoomId = consultingRoomId;
    }

    public String getQueueId() { return queueId; }
    public void setQueueId(String queueId) { this.queueId = queueId; }
    public String getConsultingRoomId() { return consultingRoomId; }
    public void setConsultingRoomId(String consultingRoomId) { this.consultingRoomId = consultingRoomId; }

    public static ClaimNextRequestBuilder builder() { return new ClaimNextRequestBuilder(); }

    public static class ClaimNextRequestBuilder {
        private String queueId;
        private String consultingRoomId;
        public ClaimNextRequestBuilder queueId(String queueId) { this.queueId = queueId; return this; }
        public ClaimNextRequestBuilder consultingRoomId(String consultingRoomId) { this.consultingRoomId = consultingRoomId; return this; }
        public ClaimNextRequest build() { return new ClaimNextRequest(queueId, consultingRoomId); }
    }
}
