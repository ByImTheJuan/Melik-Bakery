import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { MemoryRouter, Routes, Route } from "react-router-dom";
import PaymentPendingPage from "./PaymentPendingPage";
import * as paymentService from "../services/paymentService";

vi.mock("../services/paymentService", () => ({
  isOrderPayable: vi.fn(),
  createPaymentSession: vi.fn(),
}));

function renderPage(orderId = "ABC123") {
  return render(
    <MemoryRouter initialEntries={[`/payment/pending/${orderId}`]}>
      <Routes>
        <Route path="/payment/pending/:orderId" element={<PaymentPendingPage />} />
      </Routes>
    </MemoryRouter>
  );
}

describe("PaymentPendingPage", () => {
  beforeEach(() => {
    paymentService.isOrderPayable.mockReset();
    paymentService.createPaymentSession.mockReset();
    delete window.location;
    window.location = { href: "" };
  });

  it("shows the retry button when the order is still payable", async () => {
    paymentService.isOrderPayable.mockResolvedValue(true);

    renderPage();

    expect(await screen.findByRole("button", { name: "Pagar ahora" })).toBeInTheDocument();
    expect(screen.getByText("#ABC123")).toBeInTheDocument();
  });

  it("hides the retry button when the order is no longer payable", async () => {
    paymentService.isOrderPayable.mockResolvedValue(false);

    renderPage();

    await waitFor(() => expect(paymentService.isOrderPayable).toHaveBeenCalledWith("ABC123"));
    expect(screen.queryByRole("button", { name: "Pagar ahora" })).not.toBeInTheDocument();
  });

  it("treats a failed payable check as not payable instead of crashing", async () => {
    paymentService.isOrderPayable.mockRejectedValue(new Error("network error"));

    renderPage();

    await waitFor(() => expect(paymentService.isOrderPayable).toHaveBeenCalled());
    expect(screen.queryByRole("button", { name: "Pagar ahora" })).not.toBeInTheDocument();
  });

  it("redirects to Wompi when retrying payment", async () => {
    paymentService.isOrderPayable.mockResolvedValue(true);
    paymentService.createPaymentSession.mockResolvedValue({
      checkoutUrl: "https://checkout.wompi.co/p/?reference=ABC123-YYYY",
      reference: "ABC123-YYYY",
    });

    renderPage();

    const user = userEvent.setup();
    await user.click(await screen.findByRole("button", { name: "Pagar ahora" }));

    await waitFor(() => {
      expect(paymentService.createPaymentSession).toHaveBeenCalledWith("ABC123");
    });
    await waitFor(() => {
      expect(window.location.href).toBe("https://checkout.wompi.co/p/?reference=ABC123-YYYY");
    });
  });
});
