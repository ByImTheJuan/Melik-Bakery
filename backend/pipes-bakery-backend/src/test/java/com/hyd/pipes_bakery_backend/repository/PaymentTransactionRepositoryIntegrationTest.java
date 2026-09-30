package com.hyd.pipes_bakery_backend.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.hyd.pipes_bakery_backend.model.AddressSnapshot;
import com.hyd.pipes_bakery_backend.model.Order;
import com.hyd.pipes_bakery_backend.model.PaymentTransaction;

import jakarta.transaction.Transactional;

@SuppressWarnings("null")
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PaymentTransactionRepositoryIntegrationTest {

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldSaveAndFindByWompiReference() {
        Order order = saveOrder("REF001");

        PaymentTransaction transaction = new PaymentTransaction(order, "REF001-ABCD1234", 2900000L);
        paymentTransactionRepository.save(transaction);

        assertThat(paymentTransactionRepository.existsByWompiReference("REF001-ABCD1234")).isTrue();
        PaymentTransaction found = paymentTransactionRepository.findByWompiReference("REF001-ABCD1234").orElseThrow();
        assertThat(found.getOrder().getPublicId()).isEqualTo("REF001");
        assertThat(found.getAmountInCents()).isEqualTo(2900000L);
    }

    @Test
    void shouldEnforceUniqueWompiReference() {
        Order order = saveOrder("REF002");
        paymentTransactionRepository.save(new PaymentTransaction(order, "REF002-DUP", 1000L));

        PaymentTransaction duplicate = new PaymentTransaction(order, "REF002-DUP", 2000L);

        org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> {
            paymentTransactionRepository.saveAndFlush(duplicate);
        });
    }

    private Order saveOrder(String publicId) {
        Order order = new Order(
                "Felipe", "Hernandez", "felipe@melik.com", "3001234567",
                new AddressSnapshot("Calle 123", "Apto 1", "Bogota", 110111, "Colombia"),
                "Laura", new BigDecimal("10000")
        );
        order.setPublicId(publicId);
        return orderRepository.save(order);
    }
}
