import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { CartProvider } from "./CartProvider";
import { useCart } from "../hooks/useCart";
import { addItem, getCart } from "../services/cartService";
import { ensureCartId, getCartId } from "../services/cartStorage";

vi.mock("../services/cartService", () => ({
  addItem: vi.fn(),
  getCart: vi.fn(),
  removeItem: vi.fn(),
  updateItemQuantity: vi.fn(),
}));

vi.mock("../services/cartStorage", () => ({
  clearCartId: vi.fn(),
  ensureCartId: vi.fn(),
  getCartId: vi.fn(),
}));

function CartConsumer() {
  const { addToCart, cart, loading } = useCart();

  return (
    <div>
      <span>{loading ? "loading" : "ready"}</span>
      <span>{cart?.cartId || "no-cart"}</span>
      <button onClick={() => addToCart(7, 1)}>Add</button>
    </div>
  );
}

describe("CartProvider", () => {
  beforeEach(() => {
    getCartId.mockReset();
    ensureCartId.mockReset();
    getCart.mockReset();
    addItem.mockReset();
  });

  it("does not create or fetch a cart when there is no stored cart id", async () => {
    getCartId.mockReturnValue(null);

    render(
      <CartProvider>
        <CartConsumer />
      </CartProvider>
    );

    await screen.findByText("ready");

    expect(ensureCartId).not.toHaveBeenCalled();
    expect(getCart).not.toHaveBeenCalled();
    expect(screen.getByText("no-cart")).toBeInTheDocument();
  });

  it("creates the cart lazily when the first product is added", async () => {
    getCartId.mockReturnValue(null);
    ensureCartId.mockResolvedValue("cart-1");
    addItem.mockResolvedValue({ cartId: "cart-1", items: [{ productId: 7, quantity: 1 }] });

    render(
      <CartProvider>
        <CartConsumer />
      </CartProvider>
    );

    await screen.findByText("ready");
    await userEvent.click(screen.getByRole("button", { name: "Add" }));

    await waitFor(() => {
      expect(addItem).toHaveBeenCalledWith("cart-1", 7, 1);
    });

    expect(ensureCartId).toHaveBeenCalledTimes(1);
    expect(screen.getByText("cart-1")).toBeInTheDocument();
  });
});
