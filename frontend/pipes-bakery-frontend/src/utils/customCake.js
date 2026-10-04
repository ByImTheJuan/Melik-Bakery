// Helpers shared by the cake personalization page, the cart and the order views.

export const CUSTOM_CAKE_TYPE = "CUSTOM_CAKE";

export function isCustomCake(item) {
  return item?.type === CUSTOM_CAKE_TYPE;
}

// Custom cakes are separate lines identified by lineId; catalog products by productId.
export function getCartItemKey(item) {
  return isCustomCake(item) ? item.lineId : item.productId;
}

// Decorative tiers are fake, decorated tiers that make the cake look bigger.
export function formatDecorativeTiers(count) {
  if (!count) return "Sin pisos decorativos";
  return `${count} ${count === 1 ? "piso decorativo" : "pisos decorativos"}`;
}

export function formatServings(servings) {
  return servings ? `${servings} personas` : null;
}

// Short one-line description, e.g. "Mediana · Chocolate · 1 piso decorativo · Rosa fresa".
export function describeCustomCake(details) {
  if (!details) {
    return "";
  }

  return [
    details.sizeLabel,
    details.flavourLabel,
    details.decorativeTiers ? formatDecorativeTiers(details.decorativeTiers) : null,
    details.colorLabel,
  ]
    .filter(Boolean)
    .join(" · ");
}

// Mirrors CustomCakeService.price on the backend so the page can show a live estimate.
// The server recalculates the price when the cake is added to the cart.
export function estimateCakePrice(options, design) {
  if (!options || !design) {
    return 0;
  }

  const size = options.sizes.find((item) => item.id === design.sizeId);
  const flavour = options.flavours.find((item) => item.id === design.flavourId);

  if (!size || !flavour) {
    return 0;
  }

  let total =
    Number(size.basePrice) +
    Number(options.decorativeTierPrice) * (design.decorativeTiers ?? 0) +
    Number(flavour.price);

  if (design.text?.trim()) {
    total += Number(options.textPrice);
  }

  if (design.imageFile) {
    total += Number(options.imagePrice);
  }

  for (const extraId of design.extraIds ?? []) {
    const extra = options.extras.find((item) => item.id === extraId);
    total += extra ? Number(extra.price) : 0;
  }

  return Math.round(total);
}

// The customer must say whether there are dietary restrictions, and name them if there are.
export function isDietaryAnswerComplete(design) {
  if (design.hasDietaryRestrictions === null || design.hasDietaryRestrictions === undefined) return false;
  return !design.hasDietaryRestrictions || Boolean(design.dietaryRestrictions?.trim());
}

function trimmedOrNull(value) {
  return value?.trim() ? value.trim() : null;
}

// Maps the page state to the request body expected by POST /api/cart/{id}/custom-cakes.
export function toCakeConfiguration(design) {
  return {
    hasDietaryRestrictions: design.hasDietaryRestrictions,
    dietaryRestrictions: design.hasDietaryRestrictions ? trimmedOrNull(design.dietaryRestrictions) : null,
    sizeId: design.sizeId,
    flavourId: design.flavourId,
    decorativeTiers: design.decorativeTiers,
    colorId: design.colorId,
    text: trimmedOrNull(design.text),
    imageFile: design.imageFile ?? null,
    extraIds: design.extraIds,
    notes: trimmedOrNull(design.notes),
  };
}
