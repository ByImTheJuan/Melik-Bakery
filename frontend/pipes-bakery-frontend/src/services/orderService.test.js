import { beforeEach, describe, expect, it, vi } from "vitest";
import apiClient from "../api/apiClient";
import {
  ADMIN_NEXT_STATUS,
  getAllOrders,
  ORDER_STATUS_LABELS,
  ORDER_STATUS_OPTIONS,
  updateOrderStatus,
} from "./orderService";

vi.mock("../api/apiClient", () => ({
  default: {
    get: vi.fn(),
    patch: vi.fn(),
  },
}));

describe("orderService", () => {
  beforeEach(() => {
    apiClient.get.mockReset();
    apiClient.patch.mockReset();
  });

  it("exposes the expected order status metadata", () => {
    expect(ORDER_STATUS_OPTIONS).toEqual([
      "PAYMENT_PENDING",
      "PAID",
      "PREPARING",
      "SHIPPED",
      "DELIVERED",
      "CANCELLED",
    ]);
    expect(ORDER_STATUS_LABELS.CANCELLED).toBe("Cancelado");
    expect(ORDER_STATUS_LABELS.PAYMENT_PENDING).toBe("Pago pendiente");
  });

  it("exposes the admin forward-only transition map", () => {
    expect(ADMIN_NEXT_STATUS).toEqual({
      PAID: "PREPARING",
      PREPARING: "SHIPPED",
      SHIPPED: "DELIVERED",
    });
    expect(ADMIN_NEXT_STATUS.PAYMENT_PENDING).toBeUndefined();
    expect(ADMIN_NEXT_STATUS.DELIVERED).toBeUndefined();
    expect(ADMIN_NEXT_STATUS.CANCELLED).toBeUndefined();
  });

  it("loads all orders", async () => {
    apiClient.get.mockResolvedValue({ data: [{ id: "ABC123" }] });

    await expect(getAllOrders()).resolves.toEqual([{ id: "ABC123" }]);
    expect(apiClient.get).toHaveBeenCalledWith("/orders");
  });

  it("updates order status using PATCH", async () => {
    apiClient.patch.mockResolvedValue({
      data: { id: "ABC123", status: "DELIVERED" },
    });

    const result = await updateOrderStatus("ABC123", "DELIVERED");

    expect(apiClient.patch).toHaveBeenCalledWith("/orders/ABC123/status", {
      status: "DELIVERED",
    });
    expect(result).toEqual({ id: "ABC123", status: "DELIVERED" });
  });
});
