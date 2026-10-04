// Pure layout math for the 3D cake, kept free of three.js so it can be unit-tested.
// Units are arbitrary scene units: a 15 cm tier has a radius of ~1.

export const TIER_HEIGHT = 0.95;
export const STAND_HEIGHT = 0.62;
// Real tiers (up to 2, for "Grande") plus up to 2 decorative tiers
export const MAX_TIERS = 4;
// Each decorative tier is this much wider than the tier it carries
export const DECORATIVE_STEP_CM = 5;

const RADIUS_PER_CM = 0.065;

export function radiusFor(diameterCm) {
  return Math.max(0.6, diameterCm * RADIUS_PER_CM);
}

// Diameters bottom to top: the decorative (fake) tiers go underneath the real cake to make it
// look taller, each one a little wider than the one above it.
export function tierDiametersWithDecoration(realDiameters, decorativeTiers) {
  const real = realDiameters?.length ? realDiameters : [20];
  const count = Math.max(0, Math.min(decorativeTiers ?? 0, MAX_TIERS - real.length));
  const decorative = Array.from({ length: count }, (_, index) => real[0] + DECORATIVE_STEP_CM * (count - index));
  return [
    ...decorative.map((diameter) => ({ diameter, decorative: true })),
    ...real.map((diameter) => ({ diameter, decorative: false })),
  ];
}

// Returns the visible tiers bottom to top: radius, height, the y of their base and whether they're fake.
export function buildTiers(realDiameters, decorativeTiers) {
  let y = STAND_HEIGHT;
  return tierDiametersWithDecoration(realDiameters, decorativeTiers).map((tier) => {
    const built = { radius: radiusFor(tier.diameter), height: TIER_HEIGHT, y, decorative: tier.decorative };
    y += TIER_HEIGHT;
    return built;
  });
}

// Targets for all MAX_TIERS slots so hidden tiers can shrink away smoothly.
export function buildTierSlots(realDiameters, decorativeTiers) {
  const visible = buildTiers(realDiameters, decorativeTiers);
  const top = visible[visible.length - 1];

  return Array.from({ length: MAX_TIERS }, (_, index) =>
    index < visible.length
      ? { ...visible[index], presence: 1 }
      // Collapsed onto the top of the cake, ready to grow out of it
      : { radius: top.radius * 0.7, height: TIER_HEIGHT, y: top.y + top.height, presence: 0, decorative: false }
  );
}

export function topOfCake(realDiameters, decorativeTiers) {
  const tiers = buildTiers(realDiameters, decorativeTiers);
  const top = tiers[tiers.length - 1];
  return { radius: top.radius, y: top.y + top.height };
}

// Deterministic pseudo-random numbers so decorations don't jump around on every render.
export function seededRandom(seed) {
  let value = seed % 2147483647;
  if (value <= 0) value += 2147483646;
  return () => {
    value = (value * 16807) % 2147483647;
    return (value - 1) / 2147483646;
  };
}

// Angles (radians) of items spread along an arc; phi = 0 faces the camera (+z).
export function arcAngles(count, start, end) {
  if (count <= 0) return [];
  if (count === 1) return [(start + end) / 2];
  const step = (end - start) / (count - 1);
  return Array.from({ length: count }, (_, index) => start + step * index);
}
