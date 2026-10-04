import { act, render, screen, waitFor, within } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { MemoryRouter, Routes, Route } from "react-router-dom";
import PaymentResultPage from "./PaymentResultPage";
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

// Pin "today" (Bogota): the earliest delivery is then 8 Oct
vi.mock("../utils/delivery", async () => {
  const actual = await vi.importActual("../utils/delivery");
  return { ...actual, bakeryToday: () => "2026-10-04" };
});

vi.mock("../services/paymentService", () => ({
  getPaymentStatus: vi.fn(),
  retryPayment: vi.fn(),
}));

function renderPage(url = "/payment/result/MB-REF?id=txn-1") {
  return render(
    <MemoryRouter initialEntries={[url]}>
      <Routes>
        <Route path="/payment/result/:reference" element={<PaymentResultPage />} />
      </Routes>
    </MemoryRouter>
  );
}

describe("PaymentResultPage", () => {
  beforeEach(() => {
    navigateMock.mockReset();
    clearCartMock.mockReset();
    paymentService.getPaymentStatus.mockReset();
    paymentService.retryPayment.mockReset();
    useCart.mockReturnValue({ cartId: "cart-1", clearCart: clearCartMock });
    delete window.location;
    window.location = { href: "" };
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it("clears the cart and goes to the order confirmation when the payment is approved", async () => {
    const order = { id: "ABC123", clientFirstName: "Ana", items: [] };
    paymentService.getPaymentStatus.mockResolvedValue({ status: "APPROVED", orderId: "ABC123", order });

    renderPage();

    await waitFor(() => {
      expect(navigateMock).toHaveBeenCalledWith("/order/success/ABC123?ref=MB-REF", {
        replace: true,
        state: { order },
      });
    });
    expect(paymentService.getPaymentStatus).toHaveBeenCalledWith("MB-REF", "txn-1");
    expect(clearCartMock).toHaveBeenCalledTimes(1);
  });

  it("shows the failure page with retry and back-to-cart options when the payment fails", async () => {
    paymentService.getPaymentStatus.mockResolvedValue({ status: "FAILED", orderId: null });

    renderPage();

    expect(await screen.findByText("El pago no se pudo completar")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Reintentar pago" })).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Volver al carrito" })).toBeInTheDocument();
    expect(clearCartMock).not.toHaveBeenCalled();
  });

  it("goes back to the same cart from the failure page", async () => {
    paymentService.getPaymentStatus.mockResolvedValue({ status: "FAILED", orderId: null });

    renderPage();

    const user = userEvent.setup();
    await user.click(await screen.findByRole("button", { name: "Volver al carrito" }));

    expect(navigateMock).toHaveBeenCalledWith("/cart/cart-1");
  });

  it("starts a new Wompi checkout when retrying the payment", async () => {
    paymentService.getPaymentStatus.mockResolvedValue({ status: "FAILED", orderId: null });
    paymentService.retryPayment.mockResolvedValue({
      checkoutUrl: "https://checkout.wompi.co/p/?reference=MB-NEW",
      reference: "MB-NEW",
    });

    renderPage();

    const user = userEvent.setup();
    await user.click(await screen.findByRole("button", { name: "Reintentar pago" }));

    await waitFor(() => {
      expect(window.location.href).toBe("https://checkout.wompi.co/p/?reference=MB-NEW");
    });
    expect(paymentService.retryPayment).toHaveBeenCalledWith("MB-REF", undefined);
  });

  it("asks for a new delivery date when the original one is now too close, then retries with it", async () => {
    paymentService.getPaymentStatus.mockResolvedValue({ status: "FAILED", orderId: null });
    paymentService.retryPayment
      .mockRejectedValueOnce({ response: { status: 422, data: { message: "The delivery date is no longer available" } } })
      .mockResolvedValueOnce({ checkoutUrl: "https://checkout.wompi.co/p/?reference=MB-NEW", reference: "MB-NEW" });

    renderPage();

    const user = userEvent.setup();
    await user.click(await screen.findByRole("button", { name: "Reintentar pago" }));

    const dialog = await screen.findByRole("dialog", { name: "¿Cuándo quieres recibir tu pedido?" });
    expect(within(dialog).getByRole("alert")).toHaveTextContent("ya no está disponible");
    expect(window.location.href).toBe("");

    await user.click(within(dialog).getByRole("button", { name: "viernes, 9 de octubre de 2026" }));
    await user.click(within(dialog).getByRole("button", { name: "Mañana" }));

    await waitFor(() => {
      expect(window.location.href).toBe("https://checkout.wompi.co/p/?reference=MB-NEW");
    });
    expect(paymentService.retryPayment).toHaveBeenLastCalledWith("MB-REF", { date: "2026-10-09", slot: "MORNING" });
  });

  it("lets the customer close the new-date window and stay on the failure page", async () => {
    paymentService.getPaymentStatus.mockResolvedValue({ status: "FAILED", orderId: null });
    paymentService.retryPayment.mockRejectedValue({ response: { status: 422, data: {} } });

    renderPage();

    const user = userEvent.setup();
    await user.click(await screen.findByRole("button", { name: "Reintentar pago" }));
    await user.click(within(await screen.findByRole("dialog")).getByRole("button", { name: "Cerrar" }));

    expect(screen.queryByRole("dialog")).not.toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Reintentar pago" })).toBeEnabled();
  });

  it("shows an error if the retry cannot be started", async () => {
    paymentService.getPaymentStatus.mockResolvedValue({ status: "FAILED", orderId: null });
    paymentService.retryPayment.mockRejectedValue({
      response: { data: { message: "No se pudo iniciar el pago." } },
    });

    renderPage();

    const user = userEvent.setup();
    await user.click(await screen.findByRole("button", { name: "Reintentar pago" }));

    expect(await screen.findByText("No se pudo iniciar el pago.")).toBeInTheDocument();
  });

  it("keeps polling while the payment is pending and shows the failure once it is declined", async () => {
    vi.useFakeTimers({ shouldAdvanceTime: true });
    paymentService.getPaymentStatus
      .mockResolvedValueOnce({ status: "PENDING", orderId: null })
      .mockResolvedValueOnce({ status: "FAILED", orderId: null });

    renderPage();

    expect(await screen.findByText("Estamos confirmando tu pago")).toBeInTheDocument();
    expect(screen.queryByRole("button", { name: "Reintentar pago" })).not.toBeInTheDocument();

    await act(async () => {
      await vi.advanceTimersByTimeAsync(2000);
    });

    expect(await screen.findByText("El pago no se pudo completar")).toBeInTheDocument();
    expect(paymentService.getPaymentStatus).toHaveBeenCalledTimes(2);
  });

  it("keeps showing the pending page if a status check fails", async () => {
    paymentService.getPaymentStatus.mockRejectedValue(new Error("network error"));

    renderPage();

    await waitFor(() => expect(paymentService.getPaymentStatus).toHaveBeenCalled());
    expect(screen.getByText("Estamos confirmando tu pago")).toBeInTheDocument();
    expect(navigateMock).not.toHaveBeenCalled();
  });
});
