package com.example.notificationservice.web;

import com.example.notificationservice.web.NotificationController.SendReq.Operation;
import com.example.notificationservice.mail.MailService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notify")
@RequiredArgsConstructor
@Validated
public class NotificationController {

    private final MailService mail;

    @PostMapping("/created")
    public void sendCreated(@RequestBody EmailReq req) {
        mail.sendAccountCreatedRu(req.getEmail());
    }

    @PostMapping("/deleted")
    public void sendDeleted(@RequestBody EmailReq req) {
        mail.sendAccountDeletedRu(req.getEmail());
    }

    @PostMapping("/send")
    public void sendGeneric(@RequestBody SendReq req) {
        if (req.getOperation() == Operation.CREATED) {
            if (req.getName() != null && !req.getName().isBlank()) {
                mail.sendAccountCreatedRu(req.getEmail(), req.getName());
            } else {
                mail.sendAccountCreatedRu(req.getEmail());
            }
        } else if (req.getOperation() == Operation.DELETED) {
            mail.sendAccountDeletedRu(req.getEmail());
        } else {
            throw new IllegalArgumentException("Unknown operation: " + req.getOperation());
        }
    }

    @Data
    public static class EmailReq {
        @NotBlank @Email
        private String email;
    }

    @Data
    public static class SendReq {
        @NotBlank @Email
        private String email;
        @NotNull
        private Operation operation;
        private String name;
        private Long userId;

        public enum Operation { CREATED, DELETED }
    }
}

