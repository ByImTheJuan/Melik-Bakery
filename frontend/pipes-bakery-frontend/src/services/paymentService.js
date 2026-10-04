import apiClient from "../api/apiClient";

// newDelivery ({ date, slot }) is only needed when the date chosen at checkout no longer meets
// the minimum preparation time; the backend answers 422 in that case.
export async function retryPayment(reference, newDelivery) {
  const response = newDelivery
    ? await apiClient.post(`/payments/${reference}/retry`, {
        deliveryDate: newDelivery.date,
        deliverySlot: newDelivery.slot,
      })
    : await apiClient.post(`/payments/${reference}/retry`);
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
