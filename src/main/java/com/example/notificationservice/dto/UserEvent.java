package com.example.notificationservice.dto;

import lombok.*;
import java.time.Instant;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class UserEvent {
    public enum Operation { CREATED, DELETED }

    private Operation operation;
    private Long userId;
    private String email;
    private String name;
    private Instant occurredAt;
}