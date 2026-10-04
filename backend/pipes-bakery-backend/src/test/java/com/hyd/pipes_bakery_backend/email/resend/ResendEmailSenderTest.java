package com.hyd.pipes_bakery_backend.email.resend;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.hyd.pipes_bakery_backend.email.EmailMessage;
import com.hyd.pipes_bakery_backend.email.EmailProperties;
import com.hyd.pipes_bakery_backend.email.EmailSendException;

class ResendEmailSenderTest {

    private EmailProperties emailProperties;
    private RestClient.Builder builder;
    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        emailProperties = new EmailProperties();
        emailProperties.setProvider("resend");
        emailProperties.setFrom("Melik Bakery <no-reply@melikbakery.com>");
        emailProperties.getResend().setApiKey("re_test_key");
        emailProperties.getResend().setApiBaseUrl("https://api.resend.com");

        builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
    }

    @Test
    void shouldPostTheEmailToResendWithAuthAndIdempotencyKey() {
        emailProperties.setReplyTo("hola@melikbakery.com");
        ResendEmailSender sender = new ResendEmailSender(emailProperties, builder);

        server.expect(requestTo("https://api.resend.com/emails"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer re_test_key"))
                .andExpect(header("Idempotency-Key", "order-ABC123-PAID"))
                .andExpect(content().json("""
                        {
                          "from": "Melik Bakery <no-reply@melikbakery.com>",
                          "to": ["laura@example.com"],
                          "subject": "Recibimos tu pedido #ABC123",
                          "html": "<p>Hola</p>",
                          "text": "Hola",
                          "reply_to": "hola@melikbakery.com"
                        }
                        """, true))
                .andRespond(withSuccess("{\"id\":\"email-1\"}", MediaType.APPLICATION_JSON));

        sender.send(new EmailMessage("laura@example.com", "Recibimos tu pedido #ABC123", "<p>Hola</p>", "Hola", "order-ABC123-PAID"));

        server.verify();
    }

    @Test
    void shouldThrowWhenResendRejectsTheEmail() {
        ResendEmailSender sender = new ResendEmailSender(emailProperties, builder);

        server.expect(requestTo("https://api.resend.com/emails"))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"message\":\"The melikbakery.com domain is not verified\"}"));

        assertThatThrownBy(() -> sender.send(new EmailMessage("laura@example.com", "s", "<p>h</p>", "t", null)))
                .isInstanceOf(EmailSendException.class)
                .hasMessageContaining("422")
                .hasMessageContaining("not verified");
    }

    @Test
    void shouldRefuseToStartWithoutAnApiKey() {
        emailProperties.getResend().setApiKey(" ");

        assertThatThrownBy(() -> new ResendEmailSender(emailProperties, builder))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("RESEND_API_KEY");
    }
}
