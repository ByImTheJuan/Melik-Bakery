import apiClient from "../api/apiClient";

export async function retryPayment(reference) {
  const response = await apiClient.post(`/payments/${reference}/retry`);
  return response.data;
}

// transactionId is the `id` Wompi appends to the redirect URL; it lets the backend
// confirm the result with Wompi directly if its webhook has not arrived yet.
export async function getPaymentStatus(reference, transactionId) {
  const response = await apiClient.get(`/payments/${reference}`, {
    params: transactionId ? { transactionId } : undefined,
  });
  return response.data;
}
