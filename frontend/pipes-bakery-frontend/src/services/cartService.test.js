import { beforeEach, describe, expect, it, vi } from "vitest";
import apiClient from "../api/apiClient";
import { checkoutCart, createCart, getCart } from "./cartService";

vi.mock("../api/apiClient", () => ({
  default: {
    post: vi.fn(),
    get: vi.fn(),
  },
}));

describe("cartService", () => {
  beforeEach(() => {
    apiClient.post.mockReset();
    apiClient.get.mockReset();
  });

  it("creates a new cart", async () => {
    apiClient.post.mockResolvedValue({ data: { id: "cart-1" } });

    const result = await createCart();

    expect(apiClient.post).toHaveBeenCalledWith("/cart");
    expect(result).toEqual({ id: "cart-1" });
  });

  it("returns null when getCart is called without an id", async () => {
    await expect(getCart("")).resolves.toBeNull();
    expect(apiClient.get).not.toHaveBeenCalled();
  });

  it("returns null when the backend answers 404 for a cart", async () => {
    apiClient.get.mockRejectedValue({ response: { status: 404 } });

    await expect(getCart("missing")).resolves.toBeNull();
  });

  it("rethrows non-404 getCart errors", async () => {
    apiClient.get.mockRejectedValue({ response: { status: 500 } });

    await expect(getCart("broken")).rejects.toEqual({ response: { status: 500 } });
  });

  it("sends checkout data to the backend", async () => {
    const payload = { clientFirstName: "Ana" };
    apiClient.post.mockResolvedValue({ data: { id: "ABC123" } });

    const result = await checkoutCart("cart-1", payload);

    expect(apiClient.post).toHaveBeenCalledWith("/cart/cart-1/checkout", payload);
    expect(result).toEqual({ id: "ABC123" });
  });
});
