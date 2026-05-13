import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import OrdersAdmin from "./OrdersAdmin";
import * as orderService from "../../services/orderService";

vi.mock("../../services/orderService", () => ({
  getAllOrders: vi.fn(),
  updateOrderStatus: vi.fn(),
  ORDER_STATUS_OPTIONS: ["CREATED", "PAID", "SHIPPED"],
  ORDER_STATUS_LABELS: {
    CREATED: "Creado",
    PAID: "Pagado",
    SHIPPED: "Enviado",
  },
}));

const orders = [
  {
    id: "ABC123",
    status: "CREATED",
    clientFirstName: "Ana",
    clientLastName: "Lopez",
    clientEmail: "ana@melik.com",
    clientPhoneNumber: "3001234567",
    totalAmount: 22000,
    createdAt: "2026-04-30T10:00:00.000Z",
    shippingAddress: {
      street: "Calle 1",
      additionalInformation: "Apto 2",
      city: "Bogota",
      country: "Colombia",
      zipCode: 110111,
    },
    items: [
      {
        id: 1,
        productName: "Croissant",
        quantity: 2,
        unitPriceAtPurchase: 11000,
      },
    ],
  },
];

describe("OrdersAdmin", () => {
  beforeEach(() => {
    orderService.getAllOrders.mockReset();
    orderService.updateOrderStatus.mockReset();
  });

  it("loads orders and updates the selected order status", async () => {
    orderService.getAllOrders.mockResolvedValue(orders);
    orderService.updateOrderStatus.mockResolvedValue({
      ...orders[0],
      status: "SHIPPED",
    });

    render(<OrdersAdmin />);

    expect(await screen.findByText("ana@melik.com")).toBeInTheDocument();
    expect(screen.getByText("Croissant")).toBeInTheDocument();

    const user = userEvent.setup();

    await user.selectOptions(
      screen.getByRole("combobox"),
      "SHIPPED"
    );

    await user.click(
      screen.getByRole("button", {
        name: "Guardar estado",
      })
    );

    await waitFor(() => {
      expect(orderService.updateOrderStatus).toHaveBeenCalledWith("ABC123", "SHIPPED");
    });
    expect(
      await screen.findByText("Estado del pedido actualizado correctamente.")
    ).toBeInTheDocument();
  });

  it("shows a backend error when orders cannot be loaded", async () => {
    orderService.getAllOrders.mockRejectedValue({
      response: {
        data: {
          message: "No autorizado.",
        },
      },
    });

    render(<OrdersAdmin />);

    const matchingErrors = await screen.findAllByText("No autorizado.");
    expect(matchingErrors).toHaveLength(2);
  });
});
