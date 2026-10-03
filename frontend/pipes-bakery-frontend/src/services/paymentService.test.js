import { beforeEach, describe, expect, it, vi } from "vitest";
import apiClient from "../api/apiClient";
import { getPaymentStatus, retryPayment } from "./paymentService";

vi.mock("../api/apiClient", () => ({
  default: {
    post: vi.fn(),
    get: vi.fn(),
  },
}));

describe("paymentService", () => {
  beforeEach(() => {
    apiClient.post.mockReset();
    apiClient.get.mockReset();
  });

  it("retries a failed payment by reference", async () => {
    apiClient.post.mockResolvedValue({
      data: { checkoutUrl: "https://checkout.wompi.co/p/?reference=MB-NEW", reference: "MB-NEW" },
    });

    const result = await retryPayment("MB-OLD");

    expect(apiClient.post).toHaveBeenCalledWith("/payments/MB-OLD/retry");
    expect(result).toEqual({
      checkoutUrl: "https://checkout.wompi.co/p/?reference=MB-NEW",
      reference: "MB-NEW",
    });
  });

  it("gets the payment status passing Wompi's transaction id", async () => {
    apiClient.get.mockResolvedValue({ data: { status: "APPROVED", orderId: "ABC123" } });

    const result = await getPaymentStatus("MB-REF", "txn-1");

    expect(apiClient.get).toHaveBeenCalledWith("/payments/MB-REF", {
      params: { transactionId: "txn-1" },
    });
    expect(result).toEqual({ status: "APPROVED", orderId: "ABC123" });
  });

  it("gets the payment status without a transaction id", async () => {
    apiClient.get.mockResolvedValue({ data: { status: "PENDING", orderId: null } });

    await getPaymentStatus("MB-REF", null);

    expect(apiClient.get).toHaveBeenCalledWith("/payments/MB-REF", { params: undefined });
  });
});
