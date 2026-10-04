import { LuCheck } from "react-icons/lu";
import OptionField from "./OptionField";
import CakeImageDropzone from "./CakeImageDropzone";
import { formatCOP } from "../../utils/formatPrice";

// Phase 2, "Decoración": Color, Texto, Imagen and Otros elementos decorativos.
export default function DecorationPhase({
  options,
  design,
  dispatch,
  openField,
  setOpenField,
  image,
}) {
  const color = options.colors.find((item) => item.id === design.colorId);
  const toggle = (field) => setOpenField(openField === field ? null : field);
  const selectedExtras = options.extras.filter((item) => design.extraIds.includes(item.id));
  const textLength = [...design.text].length;

  return (
    <div className="phase-fields">
      <OptionField
        label="Color"
        value={color?.label ?? ""}
        open={openField === "color"}
        onToggle={() => toggle("color")}
      >
        <div className="swatch-grid" role="radiogroup" aria-label="Color de la cobertura">
          {options.colors.map((item) => (
            <button
              key={item.id}
              type="button"
              role="radio"
              aria-checked={item.id === design.colorId}
              className={`color-swatch${item.id === design.colorId ? " is-selected" : ""}`}
              onClick={() => dispatch({ type: "setColor", colorId: item.id })}
            >
              <span className="color-swatch-dot" style={{ background: item.hex }} aria-hidden="true">
                {item.id === design.colorId && <LuCheck />}
              </span>
              <span>{item.label}</span>
            </button>
          ))}
        </div>
      </OptionField>

      <OptionField
        label="Texto"
        value={design.text.trim() ? `“${design.text.trim()}”` : "Sin texto"}
        open={openField === "text"}
        onToggle={() => toggle("text")}
      >
        <label className="cake-text-field">
          <span>Mensaje escrito sobre la torta</span>
          <input
            type="text"
            value={design.text}
            maxLength={options.maxTextLength}
            placeholder="Feliz cumpleaños, Ana"
            onChange={(event) => dispatch({ type: "setText", text: event.target.value })}
          />
        </label>
        <p className="cake-field-note">
          <span>{textLength}/{options.maxTextLength} caracteres</span>
          <span>+${formatCOP(options.textPrice)}</span>
        </p>
      </OptionField>

      <OptionField
        label="Imagen"
        value={design.imagePreviewUrl ? "Foto impresa" : "Sin imagen"}
        open={openField === "image"}
        onToggle={() => toggle("image")}
      >
        <p className="cake-field-intro">
          Imprimimos tu foto en papel de azúcar sobre la torta. +${formatCOP(options.imagePrice)}
        </p>
        <CakeImageDropzone
          previewUrl={design.imagePreviewUrl}
          uploading={image.uploading}
          error={image.error}
          onSelect={image.onSelect}
          onClear={image.onClear}
        />
      </OptionField>

      <OptionField
        label="Otros elementos decorativos"
        value={selectedExtras.length ? selectedExtras.map((item) => item.label).join(", ") : "Ninguno"}
        open={openField === "extras"}
        onToggle={() => toggle("extras")}
      >
        <div className="extra-chips">
          {options.extras.map((item) => {
            const selected = design.extraIds.includes(item.id);
            return (
              <button
                key={item.id}
                type="button"
                aria-pressed={selected}
                className={`extra-chip${selected ? " is-selected" : ""}`}
                onClick={() => dispatch({ type: "toggleExtra", extraId: item.id })}
              >
                {selected && <LuCheck aria-hidden="true" />}
                <span>{item.label}</span>
                <span className="extra-chip-price">+${formatCOP(item.price)}</span>
              </button>
            );
          })}
        </div>
      </OptionField>
    </div>
  );
}
