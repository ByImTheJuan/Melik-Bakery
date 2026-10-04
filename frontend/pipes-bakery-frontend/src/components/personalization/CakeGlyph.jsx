// Tiny side-view drawing of a cake, tiers drawn to scale bottom to top.
// Decorative (fake) tiers are drawn with a dashed outline.
export default function CakeGlyph({ tiers, maxDiameter }) {
  return (
    <span className="cake-glyph" aria-hidden="true">
      {tiers.map((tier, index) => (
        <span
          key={index}
          className={tier.decorative ? "is-decorative" : undefined}
          style={{ width: `${(tier.diameter / maxDiameter) * 100}%` }}
        />
      ))}
    </span>
  );
}
