import apiClient from "../api/apiClient";

export async function createPaymentSession(orderId) {
  const response = await apiClient.post(`/payments/orders/${orderId}/sessions`);
  return response.data;
}

export async function isOrderPayable(orderId) {
  const response = await apiClient.get(`/payments/orders/${orderId}/payable`);
  return response.data.payable;
}
