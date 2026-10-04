import { render, screen, waitFor } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { MemoryRouter, Routes, Route } from "react-router-dom";
import OrderConfirmationPage from "./OrderConfirmationPage";
import * as paymentService from "../services/paymentService";

vi.mock("../services/paymentService", () => ({
  getPaymentStatus: vi.fn(),
}));

const order = {
  id: "ABC123",
  clientFirstName: "Ana",
  clientLastName: "Lopez",
  clientEmail: "ana@melik.com",
  items: [
    { id: 1, productId: 7, productName: "Croissant", quantity: 2, unitPriceAtPurchase: 9500 },
    { id: 2, productId: 8, productName: "Brookie", quantity: 1, unitPriceAtPurchase: 6000 },
  ],
  shippingCost: 10000,
  totalAmount: 35000,
};

function renderPage(entry) {
  return render(
    <MemoryRouter initialEntries={[entry]}>
      <Routes>
        <Route path="/order/success/:orderId" element={<OrderConfirmationPage />} />
      </Routes>
    </MemoryRouter>
  );
}

describe("OrderConfirmationPage", () => {
  beforeEach(() => {
    paymentService.getPaymentStatus.mockReset();
  });

  it("shows the customer, email and order lines passed from the payment result", () => {
    renderPage({ pathname: "/order/success/ABC123", search: "?ref=MB-REF", state: { order } });

    expect(screen.getByText("#ABC123")).toBeInTheDocument();
    expect(screen.getByText("Ana Lopez")).toBeInTheDocument();
    expect(screen.getByText("ana@melik.com")).toBeInTheDocument();
    expect(screen.getByText("Croissant")).toBeInTheDocument();
    expect(screen.getByText("2 × $9.500")).toBeInTheDocument();
    expect(screen.getByText("$19.000")).toBeInTheDocument();
    expect(screen.getByText("Brookie")).toBeInTheDocument();
    expect(screen.getByText("$25.000")).toBeInTheDocument();
    expect(screen.getByText("$10.000")).toBeInTheDocument();
    expect(screen.getByText("$35.000")).toBeInTheDocument();
    expect(paymentService.getPaymentStatus).not.toHaveBeenCalled();
  });

  it("tells the customer the delivery date and slot they chose", () => {
    renderPage({
      pathname: "/order/success/ABC123",
      state: { order: { ...order, deliveryDate: "2026-10-10", deliverySlot: "MORNING" } },
    });

    expect(
      screen.getByText("Ya recibimos tu pago. Prepararemos tu pedido para entregarlo el sábado, 10 de octubre de 2026 en la mañana.")
    ).toBeInTheDocument();
    expect(screen.getByText("sábado, 10 de octubre · Mañana")).toBeInTheDocument();
    expect(screen.queryByText(/lo antes posible/)).not.toBeInTheDocument();
  });

  it("reloads the order from the payment reference after a page refresh", async () => {
    paymentService.getPaymentStatus.mockResolvedValue({ status: "APPROVED", orderId: "ABC123", order });

    renderPage("/order/success/ABC123?ref=MB-REF");

    expect(await screen.findByText("Ana Lopez")).toBeInTheDocument();
    expect(paymentService.getPaymentStatus).toHaveBeenCalledWith("MB-REF");
    expect(screen.getByText("Croissant")).toBeInTheDocument();
  });

  it("ignores an order that does not match the order in the URL", async () => {
    paymentService.getPaymentStatus.mockResolvedValue({
      status: "APPROVED",
      orderId: "OTHER1",
      order: { ...order, id: "OTHER1" },
    });

    renderPage("/order/success/ABC123?ref=MB-REF");

    await waitFor(() => expect(paymentService.getPaymentStatus).toHaveBeenCalled());
    await waitFor(() =>
      expect(screen.queryByText("Cargando el detalle de tu pedido...")).not.toBeInTheDocument()
    );
    expect(screen.queryByText("Ana Lopez")).not.toBeInTheDocument();
    expect(screen.getByText("#ABC123")).toBeInTheDocument();
  });

  it("still shows the order number when no details are available", () => {
    renderPage("/order/success/ABC123");

    expect(screen.getByText("#ABC123")).toBeInTheDocument();
    expect(screen.queryByText("Detalle del pedido")).not.toBeInTheDocument();
    expect(paymentService.getPaymentStatus).not.toHaveBeenCalled();
  });
});
