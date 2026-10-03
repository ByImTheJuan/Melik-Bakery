import apiClient from "../api/apiClient";

export const ORDER_STATUS_OPTIONS = [
  "PAYMENT_PENDING",
  "PAID",
  "PREPARING",
  "SHIPPED",
  "DELIVERED",
  "CANCELLED",
];

export const ORDER_STATUS_LABELS = {
  PAYMENT_PENDING: "Pago pendiente",
  PAID: "Pagado",
  PREPARING: "En preparación",
  SHIPPED: "Enviado",
  DELIVERED: "Entregado",
  CANCELLED: "Cancelado",
};

export const ADMIN_NEXT_STATUS = {
  PAID: "PREPARING",
  PREPARING: "SHIPPED",
  SHIPPED: "DELIVERED",
};

export async function getAllOrders() {
  const response = await apiClient.get("/orders");
  return response.data;
}

export async function updateOrderStatus(orderId, status) {
  const response = await apiClient.patch(`/orders/${orderId}/status`, { status });
  return response.data;
}
