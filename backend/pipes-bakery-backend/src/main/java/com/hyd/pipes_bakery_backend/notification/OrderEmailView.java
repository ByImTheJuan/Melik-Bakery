package com.hyd.pipes_bakery_backend.notification;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.hyd.pipes_bakery_backend.dto.customcake.CustomCakeDetails;
import com.hyd.pipes_bakery_backend.model.AddressSnapshot;
import com.hyd.pipes_bakery_backend.model.DeliverySlot;
import com.hyd.pipes_bakery_backend.model.Order;
import com.hyd.pipes_bakery_backend.model.OrderItem;

/**
 * An order already formatted for an email (Spanish, COP). Built inside a transaction so the
 * templates never touch lazy JPA collections. Getters (not records) so Thymeleaf's SpEL can read them.
 */
public class OrderEmailView {

    private static final Locale SPANISH = Locale.of("es", "CO");
    private static final DateTimeFormatter DELIVERY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy", SPANISH);

    private final String publicId;
    private final String customerFirstName;
    private final String receiverName;
    private final String address;
    private final String deliveryDate;
    private final String deliverySlot;
    private final List<Line> lines;
    private final String shippingCost;
    private final String total;

    private OrderEmailView(Order order) {
        this.publicId = order.getPublicId();
        this.customerFirstName = order.getClientFirstName();
        this.receiverName = order.getReceiverName();
        this.address = formatAddress(order.getAddress());
        this.deliveryDate = order.getDeliveryDate() != null ? order.getDeliveryDate().format(DELIVERY_DATE_FORMAT) : null;
        this.deliverySlot = formatSlot(order.getDeliverySlot());
        this.lines = order.getItems().stream().map(Line::new).toList();
        this.shippingCost = formatMoney(order.getShippingCost());
        this.total = formatMoney(order.getTotalAmount());
    }

    public static OrderEmailView from(Order order) {
        return new OrderEmailView(order);
    }

    static String formatMoney(BigDecimal amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(SPANISH);
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        DecimalFormat format = new DecimalFormat("#,##0", symbols);
        return "$" + format.format(amount != null ? amount : BigDecimal.ZERO);
    }

    private static String formatSlot(DeliverySlot slot) {
        if (slot == null) {
            return null;
        }
        return switch (slot) {
            case MORNING -> "Mañana";
            case AFTERNOON -> "Tarde";
        };
    }

    private static String formatAddress(AddressSnapshot address) {
        if (address == null) {
            return null;
        }
        StringBuilder text = new StringBuilder(address.getStreet());
        if (address.getAdditionalInformation() != null && !address.getAdditionalInformation().isBlank()) {
            text.append(", ").append(address.getAdditionalInformation());
        }
        return text.append(", ").append(address.getCity()).toString();
    }

    public String getPublicId() {
        return publicId;
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getAddress() {
        return address;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public String getDeliverySlot() {
        return deliverySlot;
    }

    public List<Line> getLines() {
        return lines;
    }

    public String getShippingCost() {
        return shippingCost;
    }

    public String getTotal() {
        return total;
    }

    public static class Line {

        private final String name;
        private final int quantity;
        private final String unitPrice;
        private final String lineTotal;
        private final List<String> details;

        private Line(OrderItem item) {
            this.name = item.getItemName() != null || item.getProduct() == null
                    ? item.getItemName()
                    : item.getProduct().getName();
            this.quantity = item.getQuantity();
            this.unitPrice = formatMoney(item.getUnitPriceAtPurchase());
            this.lineTotal = formatMoney(item.calculateTotalPrice());
            this.details = item.isCustomCake() ? describe(item.getCustomCakeDetails()) : List.of();
        }

        private static List<String> describe(CustomCakeDetails cake) {
            List<String> details = new ArrayList<>();
            if (cake.getSizeLabel() != null) {
                String size = cake.getSizeLabel();
                if (cake.getServings() != null) {
                    size += " (" + cake.getServings() + " personas)";
                }
                details.add("Tamaño: " + size);
            }
            addIfPresent(details, "Sabor", cake.getFlavourLabel());
            if (cake.getDecorativeTiers() > 0) {
                details.add("Pisos decorativos: " + cake.getDecorativeTiers());
            }
            addIfPresent(details, "Color", cake.getColorLabel());
            addIfPresent(details, "Texto", cake.getText());
            if (cake.getImageFile() != null && !cake.getImageFile().isBlank()) {
                details.add("Foto comestible: sí");
            }
            if (!cake.getExtraLabels().isEmpty()) {
                details.add("Extras: " + String.join(", ", cake.getExtraLabels()));
            }
            if (cake.isHasDietaryRestrictions()) {
                addIfPresent(details, "Restricciones alimentarias", cake.getDietaryRestrictions());
            }
            addIfPresent(details, "Notas", cake.getNotes());
            return details;
        }

        private static void addIfPresent(List<String> details, String label, String value) {
            if (value != null && !value.isBlank()) {
                details.add(label + ": " + value);
            }
        }

        public String getName() {
            return name;
        }

        public int getQuantity() {
            return quantity;
        }

        public String getUnitPrice() {
            return unitPrice;
        }

        public String getLineTotal() {
            return lineTotal;
        }

        public List<String> getDetails() {
            return details;
        }
    }
}
