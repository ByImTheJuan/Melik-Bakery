import { describe, expect, it } from "vitest";
import {
  MAX_TIERS,
  STAND_HEIGHT,
  TIER_HEIGHT,
  arcAngles,
  buildTierSlots,
  buildTiers,
  radiusFor,
  tierDiametersWithDecoration,
  topOfCake,
} from "./cakeGeometry";

describe("cakeGeometry", () => {
  it("makes wider cakes wider", () => {
    expect(radiusFor(20)).toBeGreaterThan(radiusFor(15));
  });

  it("builds the real tiers of a two-tier cake bottom to top", () => {
    const tiers = buildTiers([20, 15], 0);

    expect(tiers).toHaveLength(2);
    expect(tiers[0]).toMatchObject({ radius: radiusFor(20), y: STAND_HEIGHT, decorative: false });
    expect(tiers[1].radius).toBe(radiusFor(15));
    expect(tiers[1].y).toBeCloseTo(STAND_HEIGHT + TIER_HEIGHT);
  });

  it("puts decorative tiers underneath the real cake, each one wider", () => {
    expect(tierDiametersWithDecoration([20], 2)).toEqual([
      { diameter: 30, decorative: true },
      { diameter: 25, decorative: true },
      { diameter: 20, decorative: false },
    ]);
    expect(topOfCake([20], 2).y).toBeGreaterThan(topOfCake([20], 0).y);
  });

  it("never exceeds the number of tiers the scene can draw", () => {
    expect(buildTiers([20, 15], 5)).toHaveLength(MAX_TIERS);
    expect(buildTiers([15], -1)).toHaveLength(1);
  });

  it("keeps a slot per possible tier, hiding the unused ones on top of the cake", () => {
    const slots = buildTierSlots([20], 0);

    expect(slots).toHaveLength(MAX_TIERS);
    expect(slots.map((slot) => slot.presence)).toEqual([1, 0, 0, 0]);
    expect(slots[1].y).toBeCloseTo(STAND_HEIGHT + TIER_HEIGHT);
  });

  it("spreads decorations evenly along an arc", () => {
    expect(arcAngles(3, 0, 2)).toEqual([0, 1, 2]);
    expect(arcAngles(1, 0, 2)).toEqual([1]);
    expect(arcAngles(0, 0, 2)).toEqual([]);
  });
});
