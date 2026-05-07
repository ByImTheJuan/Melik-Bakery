import apiClient from "../api/apiClient";

export const ORDER_STATUS_OPTIONS = [
  "CREATED",
  "PAID",
  "SHIPPED",
  "DELIVERED",
  "CANCELLED",
];

export const ORDER_STATUS_LABELS = {
  CREATED: "Creado",
  PAID: "Pagado",
  SHIPPED: "Enviado",
  DELIVERED: "Entregado",
  CANCELLED: "Cancelado",
};

export async function getAllOrders() {
  const response = await apiClient.get("/orders");
  return response.data;
}

export async function updateOrderStatus(orderId, status) {
  const response = await apiClient.patch(`/orders/${orderId}/status`, { status });
  return response.data;
}
