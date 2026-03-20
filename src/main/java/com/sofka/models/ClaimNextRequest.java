package com.sofka.models;

public class ClaimNextRequest {
    private String queueId;
    private String roomId;

    public ClaimNextRequest() {}

    public ClaimNextRequest(String queueId, String roomId) {
        this.queueId = queueId;
        this.roomId = roomId;
    }

    public String getQueueId() { return queueId; }
    public void setQueueId(String queueId) { this.queueId = queueId; }
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public static ClaimNextRequestBuilder builder() { return new ClaimNextRequestBuilder(); }

    public static class ClaimNextRequestBuilder {
        private String queueId;
        private String roomId;
        public ClaimNextRequestBuilder queueId(String queueId) { this.queueId = queueId; return this; }
        public ClaimNextRequestBuilder roomId(String roomId) { this.roomId = roomId; return this; }
        public ClaimNextRequest build() { return new ClaimNextRequest(queueId, roomId); }
    }
}
