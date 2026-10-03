import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { MemoryRouter, Routes, Route } from "react-router-dom";
import CheckoutPage from "./CheckoutPage";
import * as cartService from "../services/cartService";
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
    useCart.mockReturnValue({
      cart,
      cartId: "cart-1",
      clearCart: clearCartMock,
    });
    delete window.location;
    window.location = { href: "" };
  });

  it("pre-fills city and country with Bogotá, Colombia and does not allow changing them", () => {
    render(
      <MemoryRouter initialEntries={["/checkout/cart-1"]}>
        <Routes>
          <Route path="/checkout/:id" element={<CheckoutPage />} />
        </Routes>
      </MemoryRouter>
    );

    const city = screen.getByPlaceholderText("Ciudad");
    const country = screen.getByPlaceholderText("País");

    expect(city).toHaveValue("Bogotá");
    expect(country).toHaveValue("Colombia");
    expect(city).toHaveAttribute("readonly");
    expect(country).toHaveAttribute("readonly");

    fireEvent.change(city, { target: { value: "Medellín" } });
    fireEvent.change(country, { target: { value: "Perú" } });

    expect(city).toHaveValue("Bogotá");
    expect(country).toHaveValue("Colombia");
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

  it("starts the payment and redirects to Wompi without clearing the cart", async () => {
    cartService.checkoutCart.mockResolvedValue({
      checkoutUrl: "https://checkout.wompi.co/p/?reference=MB-XXXX",
      reference: "MB-XXXX",
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
    fireEvent.change(screen.getByPlaceholderText(/postal/i), {
      target: { value: "110111" },
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
          city: "Bogotá",
          zipCode: 110111,
          country: "Colombia",
        },
      });
    });

    await waitFor(() => {
      expect(window.location.href).toBe("https://checkout.wompi.co/p/?reference=MB-XXXX");
    });

    // The order only exists once the payment is approved, so the cart must survive a failed payment
    expect(clearCartMock).not.toHaveBeenCalled();
  });

  it("shows an error when starting the payment session fails", async () => {
    cartService.checkoutCart.mockRejectedValue({
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
    fireEvent.change(screen.getByPlaceholderText(/postal/i), {
      target: { value: "110111" },
    });

    fireEvent.click(screen.getByRole("button", { name: "Confirmar pedido" }));

    expect(await screen.findByText("No se pudo iniciar el pago.")).toBeInTheDocument();
  });
});
