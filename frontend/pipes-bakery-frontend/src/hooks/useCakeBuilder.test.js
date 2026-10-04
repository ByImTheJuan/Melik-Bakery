import { describe, expect, it } from "vitest";
import { INITIAL_DESIGN, cakeBuilderReducer } from "./useCakeBuilder";

describe("cakeBuilderReducer", () => {
  it("starts without assuming an answer about dietary restrictions", () => {
    expect(INITIAL_DESIGN.hasDietaryRestrictions).toBeNull();
    expect(cakeBuilderReducer(INITIAL_DESIGN, { type: "setDietaryAnswer", value: true }).hasDietaryRestrictions).toBe(true);
  });

  it("keeps decorative tiers between none and two", () => {
    expect(cakeBuilderReducer(INITIAL_DESIGN, { type: "setDecorativeTiers", count: 2 }).decorativeTiers).toBe(2);
    expect(cakeBuilderReducer(INITIAL_DESIGN, { type: "setDecorativeTiers", count: 5 }).decorativeTiers).toBe(2);
    expect(cakeBuilderReducer(INITIAL_DESIGN, { type: "setDecorativeTiers", count: -1 }).decorativeTiers).toBe(0);
  });

  it("keeps the decorative tiers when the size changes", () => {
    const withTiers = { ...INITIAL_DESIGN, decorativeTiers: 2 };
    expect(cakeBuilderReducer(withTiers, { type: "setSize", sizeId: "S" })).toMatchObject({ sizeId: "S", decorativeTiers: 2 });
  });

  it("toggles decorative elements", () => {
    const withCandles = cakeBuilderReducer(INITIAL_DESIGN, { type: "toggleExtra", extraId: "velas" });
    expect(withCandles.extraIds).toEqual(["velas"]);

    const without = cakeBuilderReducer(withCandles, { type: "toggleExtra", extraId: "velas" });
    expect(without.extraIds).toEqual([]);
  });

  it("keeps the phase within range and resets to the initial design", () => {
    expect(cakeBuilderReducer(INITIAL_DESIGN, { type: "goToPhase", phase: 9 }).phase).toBe(2);
    expect(cakeBuilderReducer(INITIAL_DESIGN, { type: "goToPhase", phase: -1 }).phase).toBe(0);

    const edited = { ...INITIAL_DESIGN, text: "Ana", notes: "Sorpresa", phase: 2 };
    expect(cakeBuilderReducer(edited, { type: "reset" })).toEqual(INITIAL_DESIGN);
  });
});
