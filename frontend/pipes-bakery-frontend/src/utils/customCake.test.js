import { describe, expect, it } from "vitest";
import {
  describeCustomCake,
  estimateCakePrice,
  formatDecorativeTiers,
  getCartItemKey,
  isCustomCake,
  isDietaryAnswerComplete,
  toCakeConfiguration,
} from "./customCake";
import { CAKE_OPTIONS } from "../test/cakeOptionsFixture";

const baseDesign = {
  hasDietaryRestrictions: false,
  dietaryRestrictions: "",
  sizeId: "M",
  flavourId: "chocolate",
  decorativeTiers: 0,
  colorId: "chantilly",
  text: "",
  imageFile: null,
  extraIds: [],
  notes: "",
};

describe("estimateCakePrice", () => {
  it("prices the size base for a simple cake", () => {
    expect(estimateCakePrice(CAKE_OPTIONS, baseDesign)).toBe(140000);
  });

  it("mirrors the backend formula for decorative tiers, flavour, text, photo and extras", () => {
    // 140000 + 2 decorative tiers 70000 + red velvet 10000 + text 8000 + photo 18000 + chispas 6000 + velas 5000
    const design = {
      ...baseDesign,
      flavourId: "red-velvet",
      decorativeTiers: 2,
      text: "Ana",
      imageFile: "custom-cakes/cake-1.jpg",
      extraIds: ["chispas", "velas"],
    };

    expect(estimateCakePrice(CAKE_OPTIONS, design)).toBe(257000);
  });

  it("ignores blank text", () => {
    expect(estimateCakePrice(CAKE_OPTIONS, { ...baseDesign, text: "   " })).toBe(140000);
  });
});

describe("dietary restrictions", () => {
  it("requires an answer, and the restriction itself when the answer is yes", () => {
    expect(isDietaryAnswerComplete({ ...baseDesign, hasDietaryRestrictions: null })).toBe(false);
    expect(isDietaryAnswerComplete({ ...baseDesign, hasDietaryRestrictions: false })).toBe(true);
    expect(isDietaryAnswerComplete({ ...baseDesign, hasDietaryRestrictions: true, dietaryRestrictions: "  " })).toBe(false);
    expect(isDietaryAnswerComplete({ ...baseDesign, hasDietaryRestrictions: true, dietaryRestrictions: "Sin gluten" })).toBe(true);
  });
});

describe("custom cake cart helpers", () => {
  const cakeLine = { type: "CUSTOM_CAKE", lineId: "line-1", productId: 0 };
  const productLine = { type: "PRODUCT", productId: 3 };

  it("identifies custom cake lines by lineId and products by productId", () => {
    expect(isCustomCake(cakeLine)).toBe(true);
    expect(isCustomCake(productLine)).toBe(false);
    expect(getCartItemKey(cakeLine)).toBe("line-1");
    expect(getCartItemKey(productLine)).toBe(3);
  });

  it("names decorative tiers", () => {
    expect(formatDecorativeTiers(0)).toBe("Sin pisos decorativos");
    expect(formatDecorativeTiers(1)).toBe("1 piso decorativo");
    expect(formatDecorativeTiers(2)).toBe("2 pisos decorativos");
  });

  it("describes a cake in one line", () => {
    expect(
      describeCustomCake({ sizeLabel: "Grande", flavourLabel: "Chocolate", decorativeTiers: 1, colorLabel: "Rosa fresa" })
    ).toBe("Grande · Chocolate · 1 piso decorativo · Rosa fresa");
    expect(describeCustomCake({ sizeLabel: "Pequeña", flavourLabel: "Vainilla", decorativeTiers: 0 })).toBe(
      "Pequeña · Vainilla"
    );
  });

  it("builds the request configuration, trimming text and dropping empty values", () => {
    expect(
      toCakeConfiguration({ ...baseDesign, text: "  Ana  ", notes: "  Sorpresa ", imagePreviewUrl: "blob:x", phase: 2 })
    ).toEqual({
      hasDietaryRestrictions: false,
      dietaryRestrictions: null,
      sizeId: "M",
      flavourId: "chocolate",
      decorativeTiers: 0,
      colorId: "chantilly",
      text: "Ana",
      imageFile: null,
      extraIds: [],
      notes: "Sorpresa",
    });
    expect(toCakeConfiguration({ ...baseDesign, text: "  " }).text).toBeNull();
    // Restrictions typed before answering "No" are not sent
    expect(toCakeConfiguration({ ...baseDesign, dietaryRestrictions: "Sin gluten" }).dietaryRestrictions).toBeNull();
    expect(
      toCakeConfiguration({ ...baseDesign, hasDietaryRestrictions: true, dietaryRestrictions: " Sin gluten " })
        .dietaryRestrictions
    ).toBe("Sin gluten");
  });
});
