import { beforeEach, describe, expect, it, vi } from "vitest";
import apiClient from "../api/apiClient";
import { createPaymentSession, isOrderPayable } from "./paymentService";

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

  it("creates a payment session for an order", async () => {
    apiClient.post.mockResolvedValue({
      data: { checkoutUrl: "https://checkout.wompi.co/p/?reference=ABC123-XXXX", reference: "ABC123-XXXX" },
    });

    const result = await createPaymentSession("ABC123");

    expect(apiClient.post).toHaveBeenCalledWith("/payments/orders/ABC123/sessions");
    expect(result).toEqual({
      checkoutUrl: "https://checkout.wompi.co/p/?reference=ABC123-XXXX",
      reference: "ABC123-XXXX",
    });
  });

  it("returns the payable boolean for an order", async () => {
    apiClient.get.mockResolvedValue({ data: { payable: true } });

    const result = await isOrderPayable("ABC123");

    expect(apiClient.get).toHaveBeenCalledWith("/payments/orders/ABC123/payable");
    expect(result).toBe(true);
  });

  it("returns false when the order is not payable", async () => {
    apiClient.get.mockResolvedValue({ data: { payable: false } });

    await expect(isOrderPayable("ABC123")).resolves.toBe(false);
  });
});
