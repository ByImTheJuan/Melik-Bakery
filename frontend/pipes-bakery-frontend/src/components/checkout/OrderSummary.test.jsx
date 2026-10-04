import { fireEvent, render, screen } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import OrderSummary from "./OrderSummary";

const cart = {
  items: [
    {
      productId: 1,
      productName: "Croissant",
      quantity: 2,
      unitPrice: 9500,
      totalPrice: 19000,
    },
  ],
  itemsTotal: 19000,
  shippingCost: 3000,
  totalPrice: 22000,
};

describe("OrderSummary", () => {
  it("renders backend totals and forwards checkout clicks", () => {
    const onCheckout = vi.fn();

    render(
      <OrderSummary
        cart={cart}
        onCheckout={onCheckout}
        loading={false}
        errors={[]}
      />
    );

    expect(screen.getByText("Resumen del pedido")).toBeInTheDocument();
    expect(screen.getByText("Subtotal")).toBeInTheDocument();
    expect(screen.getByText("$19.000")).toBeInTheDocument();
    expect(screen.getByText("$22.000")).toBeInTheDocument();

    fireEvent.click(screen.getByRole("button", { name: "Confirmar pedido" }));

    expect(onCheckout).toHaveBeenCalledTimes(1);
  });

  it("renders validation errors and a loading state", () => {
    render(
      <OrderSummary
        cart={{ ...cart, shippingCost: 0 }}
        onCheckout={vi.fn()}
        loading
        errors={["El email es obligatorio."]}
      />
    );

    expect(screen.getByText("Gratis")).toBeInTheDocument();
    expect(screen.getByText("El email es obligatorio.")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Procesando..." })).toBeDisabled();
  });

  it("renders the data consent checkbox linked to the privacy policy", () => {
    const onDataConsentChange = vi.fn();

    render(
      <OrderSummary
        cart={cart}
        onCheckout={vi.fn()}
        loading={false}
        dataConsent={false}
        onDataConsentChange={onDataConsentChange}
      />
    );

    const checkbox = screen.getByRole("checkbox", { name: /Autorizo a HYD S.A.S./ });
    expect(checkbox).not.toBeChecked();
    expect(screen.getByRole("link", { name: "Política de Privacidad" })).toHaveAttribute(
      "href",
      "/politica-de-privacidad"
    );

    fireEvent.click(checkbox);

    expect(onDataConsentChange).toHaveBeenCalledWith(true);
  });
});
