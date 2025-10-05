package com.example.notificationservice.web;

import com.example.notificationservice.mail.MailService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notify")
@RequiredArgsConstructor
@Validated
public class NotificationController {

    private final MailService mail;

    @PostMapping("/created")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sendCreated(@RequestBody EmailReq req) {
        mail.sendAccountCreated(req.getEmail());
    }

    @PostMapping("/deleted")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sendDeleted(@RequestBody EmailReq req) {
        mail.sendAccountDeleted(req.getEmail());
    }

    @Data
    public static class EmailReq {
        @NotBlank @Email
        private String email;
    }
}