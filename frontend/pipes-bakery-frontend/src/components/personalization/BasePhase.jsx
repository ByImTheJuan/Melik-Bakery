import OptionField from "./OptionField";
import CakeGlyph from "./CakeGlyph";
import DietaryField from "./DietaryField";
import { formatCOP } from "../../utils/formatPrice";
import { formatDecorativeTiers, formatServings } from "../../utils/customCake";
import { DECORATIVE_STEP_CM, tierDiametersWithDecoration } from "./cakeGeometry";

function surcharge(price) {
  return Number(price) > 0 ? `+$${formatCOP(price)}` : "Incluido";
}

// Phase 1, "Tamaño y Sabor": dietary restrictions, then Tamaño, Sabor and Pisos, in that order.
export default function BasePhase({ options, design, dispatch, openField, setOpenField, showDietaryError, dietaryRef }) {
  const size = options.sizes.find((item) => item.id === design.sizeId);
  const flavour = options.flavours.find((item) => item.id === design.flavourId);
  const toggle = (field) => setOpenField(openField === field ? null : field);

  const widestSize = Math.max(...options.sizes.flatMap((item) => item.tierDiameters));
  const decorativeCounts = Array.from({ length: options.maxDecorativeTiers + 1 }, (_, index) => index);
  const widestWithDecoration = size.tierDiameters[0] + DECORATIVE_STEP_CM * options.maxDecorativeTiers;

  return (
    <div className="phase-fields">
      <DietaryField
        design={design}
        dispatch={dispatch}
        maxLength={options.maxDietaryLength}
        showError={showDietaryError}
        fieldRef={dietaryRef}
      />

      <OptionField
        label="Tamaño"
        value={size ? `${size.label} · ${size.description}` : ""}
        open={openField === "size"}
        onToggle={() => toggle("size")}
      >
        <div className="option-grid option-grid-sizes" role="radiogroup" aria-label="Tamaño">
          {options.sizes.map((item) => (
            <button
              key={item.id}
              type="button"
              role="radio"
              aria-checked={item.id === design.sizeId}
              className={`option-card${item.id === design.sizeId ? " is-selected" : ""}`}
              onClick={() => dispatch({ type: "setSize", sizeId: item.id })}
            >
              <CakeGlyph
                tiers={item.tierDiameters.map((diameter) => ({ diameter }))}
                maxDiameter={widestSize}
              />
              <span className="option-card-title">{item.label}</span>
              <span className="option-card-meta">{item.description}</span>
              {formatServings(item.servings) && (
                <span className="option-card-meta">{formatServings(item.servings)}</span>
              )}
              <span className="option-card-price">${formatCOP(item.basePrice)}</span>
            </button>
          ))}
        </div>
      </OptionField>

      <OptionField
        label="Sabor"
        value={flavour?.label ?? ""}
        open={openField === "flavour"}
        onToggle={() => toggle("flavour")}
      >
        <div className="option-grid" role="radiogroup" aria-label="Sabor">
          {options.flavours.map((item) => (
            <button
              key={item.id}
              type="button"
              role="radio"
              aria-checked={item.id === design.flavourId}
              className={`option-card${item.id === design.flavourId ? " is-selected" : ""}`}
              onClick={() => dispatch({ type: "setFlavour", flavourId: item.id })}
            >
              <span
                className="flavour-swatch"
                aria-hidden="true"
                style={{ "--sponge": item.spongeColor, "--filling": item.fillingColor }}
              />
              <span className="option-card-title">{item.label}</span>
              <span className="option-card-price">{surcharge(item.price)}</span>
            </button>
          ))}
        </div>
      </OptionField>

      <OptionField
        label="Pisos"
        value={formatDecorativeTiers(design.decorativeTiers)}
        open={openField === "tiers"}
        onToggle={() => toggle("tiers")}
      >
        <p className="cake-field-intro">
          Los pisos son <strong>solo decorativos</strong>: bases falsas, no comestibles, cubiertas y decoradas como tu
          torta para que luzca más alta. La torta que se come es la que elegiste en Tamaño.
        </p>
        <div className="tier-options" role="radiogroup" aria-label="Pisos decorativos">
          {decorativeCounts.map((count) => (
            <button
              key={count}
              type="button"
              role="radio"
              aria-checked={count === design.decorativeTiers}
              className={`tier-option${count === design.decorativeTiers ? " is-selected" : ""}`}
              onClick={() => dispatch({ type: "setDecorativeTiers", count })}
            >
              <CakeGlyph
                tiers={tierDiametersWithDecoration(size.tierDiameters, count)}
                maxDiameter={widestWithDecoration}
              />
              <span className="option-card-title">{count === 0 ? "Ninguno" : formatDecorativeTiers(count)}</span>
              <span className="option-card-price">
                {count === 0 ? "Incluido" : `+$${formatCOP(Number(options.decorativeTierPrice) * count)}`}
              </span>
            </button>
          ))}
        </div>
        <p className="cake-field-legend">
          <span className="cake-glyph-legend" aria-hidden="true" /> Piso decorativo
        </p>
      </OptionField>
    </div>
  );
}
