package com.sofka.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActivateRoomRequest {
    private String roomId;
    private String roomName;
}
