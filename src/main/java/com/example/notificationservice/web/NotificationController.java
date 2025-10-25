package com.example.notificationservice.web;

import com.example.notificationservice.mail.MailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Notifications", description = "Отправка email-уведомлений")
@RestController
@RequestMapping("/api/v1/notify")
@RequiredArgsConstructor
@Validated
public class NotificationController {

    private final MailService mail;

    @Operation(summary = "Отправить уведомление о создании пользователя")
    @PostMapping("/created")
    public void sendCreated(@RequestBody EmailReq req) {
        mail.sendAccountCreatedRu(req.getEmail());
    }

    @Operation(summary = "Отправить уведомление об удалении пользователя")
    @PostMapping("/deleted")
    public void sendDeleted(@RequestBody EmailReq req) {
        mail.sendAccountDeletedRu(req.getEmail());
    }

    @Operation(summary = "Отправить уведомление с произвольной операцией")
    @PostMapping("/send")
    public void sendGeneric(@RequestBody SendReq req) {
        if (req.getOperation() == SendReq.Operation.CREATED) {
            mail.sendAccountCreatedRu(req.getEmail());
        } else if (req.getOperation() == SendReq.Operation.DELETED) {
            mail.sendAccountDeletedRu(req.getEmail());
        } else {
            throw new IllegalArgumentException("Unknown operation: " + req.getOperation());
        }
    }

    @Data
    public static class EmailReq {
        @Email
        @NotBlank
        private String email;
    }

    @Data
    public static class SendReq {
        @Email
        @NotBlank
        private String email;

        @NotNull
        private Operation operation;

        private String name;
        private Long userId;

        public enum Operation { CREATED, DELETED }
    }
}