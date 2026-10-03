import { act, render, screen, waitFor } from "@testing-library/react";
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
    expect(paymentService.retryPayment).toHaveBeenCalledWith("MB-REF");
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
