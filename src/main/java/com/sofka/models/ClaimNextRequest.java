package com.sofka.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClaimNextRequest {
    private String queueId;
    private String roomId;
}
