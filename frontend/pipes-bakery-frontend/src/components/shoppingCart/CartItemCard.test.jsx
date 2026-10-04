import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import CartItemCard from "./CartItemCard";
import { useCart } from "../../hooks/useCart";

vi.mock("../../hooks/useCart", () => ({ useCart: vi.fn() }));

const cart = {
  removeFromCart: vi.fn(),
  updateQuantity: vi.fn(),
  removeCustomCake: vi.fn(),
  updateCustomCakeQuantity: vi.fn(),
};

const cakeLine = {
  type: "CUSTOM_CAKE",
  lineId: "line-1",
  productId: 0,
  productName: "Torta personalizada",
  quantity: 1,
  unitPriceAtAdd: 140000,
  productImage: null,
  customCake: { sizeLabel: "Mediana", flavourLabel: "Chocolate", decorativeTiers: 2, colorLabel: "Rosa fresa" },
};

describe("CartItemCard", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    useCart.mockReturnValue(cart);
  });

  it("shows a custom cake with its details and manages it by lineId", async () => {
    const user = userEvent.setup();
    render(<CartItemCard cartItem={cakeLine} />);

    expect(screen.getByText("Torta personalizada")).toBeInTheDocument();
    expect(screen.getByText("Mediana · Chocolate · 2 pisos decorativos · Rosa fresa")).toBeInTheDocument();

    await user.click(screen.getByText("+"));
    expect(cart.updateCustomCakeQuantity).toHaveBeenCalledWith("line-1", 2);

    await user.click(screen.getByText("X"));
    expect(cart.removeCustomCake).toHaveBeenCalledWith("line-1");
    expect(cart.removeFromCart).not.toHaveBeenCalled();
  });

  it("keeps managing catalog products by productId", async () => {
    const user = userEvent.setup();
    render(
      <CartItemCard
        cartItem={{ productId: 3, productName: "Brookie", quantity: 2, unitPriceAtAdd: 9000, productImage: "brookie.jpg" }}
      />
    );

    await user.click(screen.getByText("-"));
    expect(cart.updateQuantity).toHaveBeenCalledWith(3, 1);
    expect(cart.updateCustomCakeQuantity).not.toHaveBeenCalled();
  });
});
