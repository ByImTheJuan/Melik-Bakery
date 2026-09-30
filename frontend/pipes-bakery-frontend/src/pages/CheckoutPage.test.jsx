import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { MemoryRouter, Routes, Route } from "react-router-dom";
import CheckoutPage from "./CheckoutPage";
import * as cartService from "../services/cartService";
import * as paymentService from "../services/paymentService";
import { useCart } from "../hooks/useCart";

const clearCartMock = vi.fn();
const navigateMock = vi.fn();

vi.mock("react-router-dom", async () => {
  const actual = await vi.importActual("react-router-dom");
  return {
    ...actual,
    useNavigate: () => navigateMock,
  };
});

vi.mock("../hooks/useCart", () => ({
  useCart: vi.fn(),
}));

vi.mock("../services/cartService", () => ({
  checkoutCart: vi.fn(),
}));

vi.mock("../services/paymentService", () => ({
  createPaymentSession: vi.fn(),
}));

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

describe("CheckoutPage", () => {
  beforeEach(() => {
    navigateMock.mockReset();
    clearCartMock.mockReset();
    cartService.checkoutCart.mockReset();
    paymentService.createPaymentSession.mockReset();
    useCart.mockReturnValue({
      cart,
      cartId: "cart-1",
      clearCart: clearCartMock,
    });
    delete window.location;
    window.location = { href: "" };
  });

  it("shows client-side validation errors without calling the backend", async () => {
    render(
      <MemoryRouter initialEntries={["/checkout/cart-1"]}>
        <Routes>
          <Route path="/checkout/:id" element={<CheckoutPage />} />
        </Routes>
      </MemoryRouter>
    );

    fireEvent.click(screen.getByRole("button", { name: "Confirmar pedido" }));

    expect(await screen.findByText("El nombre es obligatorio.")).toBeInTheDocument();
    expect(cartService.checkoutCart).not.toHaveBeenCalled();
  });

  it("submits a valid checkout and redirects to the Wompi checkout URL", async () => {
    cartService.checkoutCart.mockResolvedValue({ id: "ABC123" });
    clearCartMock.mockResolvedValue();
    paymentService.createPaymentSession.mockResolvedValue({
      checkoutUrl: "https://checkout.wompi.co/p/?reference=ABC123-XXXX",
      reference: "ABC123-XXXX",
    });

    render(
      <MemoryRouter initialEntries={["/checkout/cart-1"]}>
        <Routes>
          <Route path="/checkout/:id" element={<CheckoutPage />} />
        </Routes>
      </MemoryRouter>
    );

    fireEvent.change(screen.getByPlaceholderText("Nombre"), {
      target: { value: "Ana" },
    });
    fireEvent.change(screen.getByPlaceholderText("Apellido"), {
      target: { value: "Lopez" },
    });
    fireEvent.change(screen.getByPlaceholderText("Email"), {
      target: { value: "ana@melik.com" },
    });
    fireEvent.change(screen.getByPlaceholderText(/Tel/), {
      target: { value: "3001234567" },
    });
    fireEvent.change(screen.getByPlaceholderText(/Dire/), {
      target: { value: "Calle 123" },
    });
    fireEvent.change(screen.getByPlaceholderText("Ciudad"), {
      target: { value: "Bogota" },
    });
    fireEvent.change(screen.getByPlaceholderText(/postal/i), {
      target: { value: "110111" },
    });
    fireEvent.change(screen.getByPlaceholderText(/Pa/), {
      target: { value: "Colombia" },
    });

    fireEvent.click(screen.getByRole("button", { name: "Confirmar pedido" }));

    await waitFor(() => {
      expect(cartService.checkoutCart).toHaveBeenCalledWith("cart-1", {
        clientFirstName: "Ana",
        clientLastName: "Lopez",
        clientEmail: "ana@melik.com",
        clientPhoneNumber: "3001234567",
        receiverName: null,
        shippingAddress: {
          street: "Calle 123",
          additionalInformation: null,
          city: "Bogota",
          zipCode: 110111,
          country: "Colombia",
        },
      });
    });

    expect(clearCartMock).toHaveBeenCalledTimes(1);

    await waitFor(() => {
      expect(paymentService.createPaymentSession).toHaveBeenCalledWith("ABC123");
    });

    await waitFor(() => {
      expect(window.location.href).toBe("https://checkout.wompi.co/p/?reference=ABC123-XXXX");
    });
  });

  it("shows an error when starting the payment session fails", async () => {
    cartService.checkoutCart.mockResolvedValue({ id: "ABC123" });
    clearCartMock.mockResolvedValue();
    paymentService.createPaymentSession.mockRejectedValue({
      response: { data: { message: "No se pudo iniciar el pago." } },
    });

    render(
      <MemoryRouter initialEntries={["/checkout/cart-1"]}>
        <Routes>
          <Route path="/checkout/:id" element={<CheckoutPage />} />
        </Routes>
      </MemoryRouter>
    );

    fireEvent.change(screen.getByPlaceholderText("Nombre"), {
      target: { value: "Ana" },
    });
    fireEvent.change(screen.getByPlaceholderText("Apellido"), {
      target: { value: "Lopez" },
    });
    fireEvent.change(screen.getByPlaceholderText("Email"), {
      target: { value: "ana@melik.com" },
    });
    fireEvent.change(screen.getByPlaceholderText(/Tel/), {
      target: { value: "3001234567" },
    });
    fireEvent.change(screen.getByPlaceholderText(/Dire/), {
      target: { value: "Calle 123" },
    });
    fireEvent.change(screen.getByPlaceholderText("Ciudad"), {
      target: { value: "Bogota" },
    });
    fireEvent.change(screen.getByPlaceholderText(/postal/i), {
      target: { value: "110111" },
    });
    fireEvent.change(screen.getByPlaceholderText(/Pa/), {
      target: { value: "Colombia" },
    });

    fireEvent.click(screen.getByRole("button", { name: "Confirmar pedido" }));

    expect(await screen.findByText("No se pudo iniciar el pago.")).toBeInTheDocument();
  });
});
