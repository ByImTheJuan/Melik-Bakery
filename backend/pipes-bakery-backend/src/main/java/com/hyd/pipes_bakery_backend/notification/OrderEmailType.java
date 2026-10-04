package com.hyd.pipes_bakery_backend.notification;

import java.util.Optional;

import com.hyd.pipes_bakery_backend.model.OrderStatus;

/** The customer email sent for each order status, with its copy. Statuses not listed send nothing. */
public enum OrderEmailType {

    RECEIVED(OrderStatus.PAID,
            "Recibimos tu pedido #%s",
            "¡Recibimos tu pedido!",
            "Tu pago fue aprobado y ya tenemos tu pedido. Te escribiremos cada vez que avance."),

    PREPARING(OrderStatus.PREPARING,
            "Tu pedido #%s está en preparación",
            "Tu pedido está en preparación",
            "Nuestro equipo ya está horneando y preparando tu pedido con mucho cariño."),

    SHIPPED(OrderStatus.SHIPPED,
            "Tu pedido #%s va en camino",
            "¡Tu pedido va en camino!",
            "Tu pedido salió de nuestra pastelería y pronto llegará a la dirección de entrega."),

    DELIVERED(OrderStatus.DELIVERED,
            "Tu pedido #%s fue entregado",
            "Tu pedido fue entregado",
            "Esperamos que lo disfrutes. ¡Gracias por elegir Melik Bakery!"),

    CANCELLED(OrderStatus.CANCELLED,
            "Tu pedido #%s fue cancelado",
            "Tu pedido fue cancelado",
            "Tu pedido fue cancelado. Si tienes alguna pregunta o no esperabas este cambio, contáctanos.");

    private final OrderStatus status;
    private final String subjectPattern;
    private final String headline;
    private final String message;

    OrderEmailType(OrderStatus status, String subjectPattern, String headline, String message) {
        this.status = status;
        this.subjectPattern = subjectPattern;
        this.headline = headline;
        this.message = message;
    }

    public static Optional<OrderEmailType> forStatus(OrderStatus status) {
        for (OrderEmailType type : values()) {
            if (type.status == status) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }

    public String subject(String orderPublicId) {
        return String.format(subjectPattern, orderPublicId);
    }

    public String getHeadline() {
        return headline;
    }

    public String getMessage() {
        return message;
    }
}
