package com.example.notificationservice;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class NotificationApiIT {

    @RegisterExtension
    static GreenMailExtension greenMail =
            new GreenMailExtension(ServerSetupTest.SMTP)
                    .withPerMethodLifecycle(true);

    @Value("${local.server.port}")
    int port;

    @Test
    void directApiSend_shouldSendMail() throws Exception {
        var json = """
        {
          "email": "carol@example.com",
          "operation": "CREATED",
          "name": "Carol",
          "userId": 100
        }
        """;

        given()
                .port(port)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(json)
                .when()
                .post("/api/v1/notify/send")
                .then()
                .statusCode(200);

        greenMail.waitForIncomingEmail(5000, 1);
        MimeMessage[] messages = greenMail.getReceivedMessages();

        assertThat(messages).hasSize(1);
        assertThat(messages[0].getAllRecipients()[0].toString()).isEqualTo("carol@example.com");
        assertThat(messages[0].getSubject()).contains("Аккаунт создан");
    }
}
