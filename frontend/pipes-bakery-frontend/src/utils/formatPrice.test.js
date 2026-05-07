import { describe, expect, it } from "vitest";
import { formatCOP } from "./formatPrice";

describe("formatCOP", () => {
  it("formats integer values with Colombian separators", () => {
    expect(formatCOP(12500)).toBe("12.500");
  });

  it("formats zero correctly", () => {
    expect(formatCOP(0)).toBe("0");
  });
});
