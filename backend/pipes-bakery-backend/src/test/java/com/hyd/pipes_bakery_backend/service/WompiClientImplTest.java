package com.hyd.pipes_bakery_backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyd.pipes_bakery_backend.config.WompiProperties;
import com.hyd.pipes_bakery_backend.dto.payment.WompiTransactionDTO;
import com.hyd.pipes_bakery_backend.dto.payment.WompiWebhookEventDTO;

class WompiClientImplTest {

    private WompiProperties wompiProperties;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        wompiProperties = new WompiProperties();
        wompiProperties.setPublicKey("pub_test_key");
        wompiProperties.setIntegritySecret("test-secret");
        wompiProperties.setEventsSecret("test-events-secret");
        wompiProperties.setApiBaseUrl("https://sandbox.wompi.co/v1");
        wompiProperties.setCheckoutBaseUrl("https://checkout.wompi.co/p/");
        wompiProperties.setRedirectUrl("http://localhost:5173/payment/pending");
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldBuildIntegritySignatureMatchingIndependentlyComputedHash() {
        WompiClientImpl client = new WompiClientImpl(wompiProperties, RestClient.builder());

        String signature = client.buildIntegritySignature("REF123", 1000000L, "COP");

        assertThat(signature).isEqualTo("80df7d7462b22583472c3bf5ee391c7bcd41442d7b49ff61e4747515c2ed46ac");
    }

    @Test
    void shouldBuildDeterministicSignatureForSameInputs() {
        WompiClientImpl client = new WompiClientImpl(wompiProperties, RestClient.builder());

        String first = client.buildIntegritySignature("REF123", 1000000L, "COP");
        String second = client.buildIntegritySignature("REF123", 1000000L, "COP");
        String different = client.buildIntegritySignature("REF456", 1000000L, "COP");

        assertThat(first).isEqualTo(second);
        assertThat(first).isNotEqualTo(different);
        assertThat(first).matches("[0-9a-f]{64}");
    }

    @Test
    void shouldBuildCheckoutUrlWithExpectedQueryParameters() {
        WompiClientImpl client = new WompiClientImpl(wompiProperties, RestClient.builder());

        String url = client.buildCheckoutUrl("REF123", 1000000L, "COP", "AB12CD");

        assertThat(url).startsWith("https://checkout.wompi.co/p/");
        assertThat(url).contains("public-key=pub_test_key");
        assertThat(url).contains("currency=COP");
        assertThat(url).contains("amount-in-cents=1000000");
        assertThat(url).contains("reference=REF123");
        assertThat(url).contains("redirect-url=http://localhost:5173/payment/pending/AB12CD");
        assertThat(url).contains("signature:integrity=80df7d7462b22583472c3bf5ee391c7bcd41442d7b49ff61e4747515c2ed46ac");
    }

    @Test
    void shouldVerifyValidEventChecksum() throws Exception {
        WompiClientImpl client = new WompiClientImpl(wompiProperties, RestClient.builder());
        WompiWebhookEventDTO event = buildEvent(
                "0da358d20999968f3d2883a927c287dfc49ff0e1c9251c673f27e34fc141e47e");

        assertThat(client.verifyEventChecksum(event)).isTrue();
    }

    @Test
    void shouldRejectTamperedEventChecksum() throws Exception {
        WompiClientImpl client = new WompiClientImpl(wompiProperties, RestClient.builder());
        WompiWebhookEventDTO event = buildEvent("0000000000000000000000000000000000000000000000000000000000000000");

        assertThat(client.verifyEventChecksum(event)).isFalse();
    }

    @Test
    void shouldFetchTransactionFromWompiApi() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        WompiClientImpl client = new WompiClientImpl(wompiProperties, builder);

        server.expect(requestTo("https://sandbox.wompi.co/v1/transactions/txn-1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(
                        "{\"data\":{\"id\":\"txn-1\",\"reference\":\"REF123\",\"status\":\"APPROVED\",\"amount_in_cents\":1000000}}",
                        MediaType.APPLICATION_JSON));

        WompiTransactionDTO transaction = client.fetchTransaction("txn-1");

        assertThat(transaction.getId()).isEqualTo("txn-1");
        assertThat(transaction.getReference()).isEqualTo("REF123");
        assertThat(transaction.getStatus()).isEqualTo("APPROVED");
        assertThat(transaction.getAmountInCents()).isEqualTo(1000000L);
        server.verify();
    }

    private WompiWebhookEventDTO buildEvent(String checksum) throws Exception {
        JsonNode data = objectMapper.readTree(
                "{\"transaction\":{\"id\":\"1234\",\"status\":\"APPROVED\"}}");

        WompiWebhookEventDTO event = new WompiWebhookEventDTO();
        event.setEvent("transaction.updated");
        event.setData(data);
        event.setTimestamp(1700000000L);

        WompiWebhookEventDTO.Signature signature = new WompiWebhookEventDTO.Signature();
        signature.setProperties(java.util.List.of("transaction.id", "transaction.status"));
        signature.setChecksum(checksum);
        event.setSignature(signature);

        return event;
    }
}
