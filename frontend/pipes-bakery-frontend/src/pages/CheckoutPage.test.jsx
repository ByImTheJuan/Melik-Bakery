import { fireEvent, render, screen, waitFor, within } from "@testing-library/react";
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

// Pin "today" (Bogota) so the calendar is deterministic: the earliest delivery is 8 Oct
vi.mock("../utils/delivery", async () => {
  const actual = await vi.importActual("../utils/delivery");
  return { ...actual, bakeryToday: () => "2026-10-04" };
});

vi.mock("../services/cartService", () => ({
  checkoutCart: vi.fn(),
}));

function renderCheckout() {
  return render(
    <MemoryRouter initialEntries={["/checkout/cart-1"]}>
      <Routes>
        <Route path="/checkout/:id" element={<CheckoutPage />} />
      </Routes>
    </MemoryRouter>
  );
}

function chooseDelivery(dayLabel = "viernes, 9 de octubre de 2026", slot = "Tarde") {
  const dialog = screen.getByRole("dialog");
  fireEvent.click(within(dialog).getByRole("button", { name: dayLabel }));
  fireEvent.click(within(dialog).getByRole("button", { name: slot }));
}

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
    expect(screen.getByText("Elige la fecha y la franja de entrega.")).toBeInTheDocument();
    expect(
      screen.getByText("Debes autorizar el tratamiento de tus datos personales para continuar.")
    ).toBeInTheDocument();
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

    chooseDelivery();
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

    fireEvent.click(screen.getByRole("checkbox", { name: /Autorizo a HYD S.A.S./ }));
    fireEvent.click(screen.getByRole("button", { name: "Confirmar pedido" }));

    await waitFor(() => {
      expect(cartService.checkoutCart).toHaveBeenCalledWith("cart-1", {
        clientFirstName: "Ana",
        clientLastName: "Lopez",
        clientEmail: "ana@melik.com",
        clientPhoneNumber: "3001234567",
        receiverName: null,
        deliveryDate: "2026-10-09",
        deliverySlot: "AFTERNOON",
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

  it("asks for the delivery date as soon as the customer arrives, from 4 days after today", () => {
    renderCheckout();

    const dialog = screen.getByRole("dialog", { name: "¿Cuándo quieres recibir tu pedido?" });
    expect(within(dialog).getByText(/al menos 4 días/)).toBeInTheDocument();
    expect(within(dialog).getByRole("button", { name: /miércoles, 7 de octubre de 2026/ })).toBeDisabled();
    expect(within(dialog).getByRole("button", { name: "jueves, 8 de octubre de 2026" })).toBeEnabled();
    // Without a delivery chosen it can't be dismissed, only left to go back to the cart
    expect(within(dialog).queryByRole("button", { name: "Cerrar" })).not.toBeInTheDocument();
    fireEvent.keyDown(dialog, { key: "Escape" });
    expect(screen.getByRole("dialog")).toBeInTheDocument();
  });

  it("closes once a valid date and slot are chosen and keeps them visible above the form", () => {
    renderCheckout();

    chooseDelivery("jueves, 8 de octubre de 2026", "Mañana");

    expect(screen.queryByRole("dialog")).not.toBeInTheDocument();
    const field = screen.getByRole("button", { name: /Entrega/ });
    expect(field).toHaveTextContent("jueves, 8 de octubre de 2026 · Mañana");
    expect(
      field.compareDocumentPosition(screen.getByPlaceholderText("Nombre")) & Node.DOCUMENT_POSITION_FOLLOWING
    ).toBeTruthy();
  });

  it("reopens from the field to change the date and time", () => {
    renderCheckout();
    chooseDelivery("jueves, 8 de octubre de 2026", "Mañana");

    fireEvent.click(screen.getByRole("button", { name: /Entrega/ }));
    const dialog = screen.getByRole("dialog");
    expect(within(dialog).getByRole("button", { name: "jueves, 8 de octubre de 2026" })).toHaveAttribute(
      "aria-pressed",
      "true"
    );

    // Changing only the date keeps the dialog open so the slot can be changed too
    fireEvent.click(within(dialog).getByRole("button", { name: "sábado, 10 de octubre de 2026" }));
    expect(screen.getByRole("dialog")).toBeInTheDocument();
    fireEvent.click(within(dialog).getByRole("button", { name: "Tarde" }));

    expect(screen.queryByRole("dialog")).not.toBeInTheDocument();
    expect(screen.getByRole("button", { name: /Entrega/ })).toHaveTextContent("sábado, 10 de octubre de 2026 · Tarde");

    // Once a delivery exists the dialog can be dismissed without changes
    fireEvent.click(screen.getByRole("button", { name: /Entrega/ }));
    fireEvent.keyDown(screen.getByRole("dialog"), { key: "Escape" });
    expect(screen.queryByRole("dialog")).not.toBeInTheDocument();
  });

  it("lets the customer go back to the cart from the dialog", () => {
    renderCheckout();

    fireEvent.click(within(screen.getByRole("dialog")).getByRole("button", { name: "Volver al carrito" }));

    expect(navigateMock).toHaveBeenCalledWith("/cart/cart-1");
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

    chooseDelivery();
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

    fireEvent.click(screen.getByRole("checkbox", { name: /Autorizo a HYD S.A.S./ }));
    fireEvent.click(screen.getByRole("button", { name: "Confirmar pedido" }));

    expect(await screen.findByText("No se pudo iniciar el pago.")).toBeInTheDocument();
  });
});
