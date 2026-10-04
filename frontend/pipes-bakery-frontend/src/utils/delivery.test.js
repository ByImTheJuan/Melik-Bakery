import { describe, expect, it } from "vitest";
import {
  addDays,
  bakeryToday,
  deliveryRange,
  formatDeliveryDate,
  isDeliveryComplete,
  isDeliveryDateAllowed,
  monthGrid,
  slotLabel,
  slotPhrase,
  formatMonth,
} from "./delivery";

describe("delivery dates", () => {
  it("uses the date in Bogota, not the date on the device", () => {
    // 03:00 UTC on 5 Oct is still 22:00 on 4 Oct in Bogota
    expect(bakeryToday(new Date("2026-10-05T03:00:00Z"))).toBe("2026-10-04");
    expect(bakeryToday(new Date("2026-10-05T06:00:00Z"))).toBe("2026-10-05");
  });

  it("allows deliveries from four days after today up to ninety days ahead", () => {
    expect(deliveryRange("2026-10-04")).toEqual({ min: "2026-10-08", max: "2027-01-02" });
    expect(isDeliveryDateAllowed("2026-10-07", "2026-10-04")).toBe(false);
    expect(isDeliveryDateAllowed("2026-10-08", "2026-10-04")).toBe(true);
    expect(isDeliveryDateAllowed("2027-01-03", "2026-10-04")).toBe(false);
    expect(isDeliveryDateAllowed(null, "2026-10-04")).toBe(false);
  });

  it("needs both a valid date and a slot", () => {
    expect(isDeliveryComplete({ date: "2026-10-09", slot: "MORNING" }, "2026-10-04")).toBe(true);
    expect(isDeliveryComplete({ date: "2026-10-09", slot: null }, "2026-10-04")).toBe(false);
    expect(isDeliveryComplete({ date: "2026-10-05", slot: "MORNING" }, "2026-10-04")).toBe(false);
  });

  it("adds days across month and year boundaries", () => {
    expect(addDays("2026-12-30", 3)).toBe("2027-01-02");
  });

  it("builds Monday-first month grids", () => {
    const weeks = monthGrid("2026-10-01");
    // 1 Oct 2026 is a Thursday
    expect(weeks[0]).toEqual([null, null, null, "2026-10-01", "2026-10-02", "2026-10-03", "2026-10-04"]);
    expect(weeks.flat().filter(Boolean)).toHaveLength(31);
    expect(weeks.every((week) => week.length === 7)).toBe(true);
  });

  it("formats dates and slots in Spanish", () => {
    expect(formatDeliveryDate("2026-10-10")).toBe("sábado, 10 de octubre de 2026");
    expect(formatDeliveryDate("2026-10-10", { withYear: false })).toBe("sábado, 10 de octubre");
    expect(formatMonth("2026-10-10")).toBe("Octubre de 2026");
    expect(slotLabel("MORNING")).toBe("Mañana");
    expect(slotPhrase("AFTERNOON")).toBe("en la tarde");
  });
});
