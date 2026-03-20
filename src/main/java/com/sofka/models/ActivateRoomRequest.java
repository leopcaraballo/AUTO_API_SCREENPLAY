package com.sofka.models;

public class ActivateRoomRequest {
    private String roomId;
    private String roomName;

    public ActivateRoomRequest() {}

    public ActivateRoomRequest(String roomId, String roomName) {
        this.roomId = roomId;
        this.roomName = roomName;
    }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public static ActivateRoomRequestBuilder builder() { return new ActivateRoomRequestBuilder(); }

    public static class ActivateRoomRequestBuilder {
        private String roomId;
        private String roomName;
        public ActivateRoomRequestBuilder roomId(String roomId) { this.roomId = roomId; return this; }
        public ActivateRoomRequestBuilder roomName(String roomName) { this.roomName = roomName; return this; }
        public ActivateRoomRequest build() { return new ActivateRoomRequest(roomId, roomName); }
    }
}
