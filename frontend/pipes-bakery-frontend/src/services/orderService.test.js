import { beforeEach, describe, expect, it, vi } from "vitest";
import apiClient from "../api/apiClient";
import {
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
    expect(ORDER_STATUS_OPTIONS).toContain("SHIPPED");
    expect(ORDER_STATUS_LABELS.CANCELLED).toBe("Cancelado");
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
