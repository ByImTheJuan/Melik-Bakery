import { useId } from "react";
import { LuMinus, LuPlus, LuShoppingBag } from "react-icons/lu";
import { formatCOP } from "../../utils/formatPrice";
import { estimateCakePrice, formatDecorativeTiers, formatServings } from "../../utils/customCake";

const MAX_QUANTITY = 10;

function money(value) {
  return `$${formatCOP(value)}`;
}

function surcharge(value) {
  return Number(value) > 0 ? `+${money(value)}` : "Incluido";
}

// Phase 3, "Resumen": every choice with its price, notes for the baker, quantity and add to cart.
export default function CakeSummary({
  options,
  design,
  dispatch,
  quantity,
  onQuantityChange,
  onEdit,
  onAddToCart,
  adding,
}) {
  const notesId = useId();
  const size = options.sizes.find((item) => item.id === design.sizeId);
  const flavour = options.flavours.find((item) => item.id === design.flavourId);
  const color = options.colors.find((item) => item.id === design.colorId);
  const extras = options.extras.filter((item) => design.extraIds.includes(item.id));
  const text = design.text.trim();
  const unitPrice = estimateCakePrice(options, design);
  const sizeValue = [size.label, size.description, formatServings(size.servings)].filter(Boolean).join(" · ");

  const rows = [
    {
      label: "Restricciones",
      value: design.hasDietaryRestrictions ? design.dietaryRestrictions.trim() : "Ninguna",
      price: "",
      phase: 0,
      field: null,
    },
    { label: "Tamaño", value: sizeValue, price: money(size.basePrice), phase: 0, field: "size" },
    { label: "Sabor", value: flavour.label, price: surcharge(flavour.price), phase: 0, field: "flavour" },
    {
      label: "Pisos",
      value: formatDecorativeTiers(design.decorativeTiers),
      price: design.decorativeTiers ? surcharge(Number(options.decorativeTierPrice) * design.decorativeTiers) : "—",
      phase: 0,
      field: "tiers",
    },
    { label: "Color", value: color.label, price: "Incluido", phase: 1, field: "color" },
    { label: "Texto", value: text ? `“${text}”` : "Sin texto", price: text ? surcharge(options.textPrice) : "—", phase: 1, field: "text" },
    {
      label: "Imagen",
      value: design.imageFile ? "Foto impresa" : "Sin imagen",
      price: design.imageFile ? surcharge(options.imagePrice) : "—",
      phase: 1,
      field: "image",
    },
    {
      label: "Decoración",
      value: extras.length ? extras.map((item) => item.label).join(", ") : "Ninguna",
      price: extras.length ? surcharge(extras.reduce((sum, item) => sum + Number(item.price), 0)) : "—",
      phase: 1,
      field: "extras",
    },
  ];

  return (
    <div className="cake-summary">
      <dl className="cake-summary-rows">
        {rows.map((row) => (
          <div key={row.label} className="cake-summary-row">
            <dt>{row.label}</dt>
            <dd>
              <span className="cake-summary-value">{row.value}</span>
              <span className="cake-summary-price">{row.price}</span>
              <button
                type="button"
                className="cake-link-button"
                aria-label={`Editar ${row.label.toLowerCase()}`}
                onClick={() => onEdit(row.phase, row.field)}
              >
                Editar
              </button>
            </dd>
          </div>
        ))}
      </dl>

      <label className="cake-notes" htmlFor={notesId}>
        <span className="cake-notes-label">Notas para el pastelero <small>(opcional)</small></span>
        <textarea
          id={notesId}
          rows={3}
          value={design.notes}
          maxLength={options.maxNotesLength}
          placeholder="Ej.: es una sorpresa, prefiero las letras en dorado, la entrega es a las 4 p. m."
          onChange={(event) => dispatch({ type: "setNotes", notes: event.target.value })}
        />
        <small className="cake-field-note">
          <span>{[...design.notes].length}/{options.maxNotesLength} caracteres</span>
        </small>
      </label>

      <div className="cake-summary-total">
        <div className="quantity-stepper" aria-label="Cantidad">
          <button
            type="button"
            aria-label="Quitar una torta"
            disabled={quantity <= 1}
            onClick={() => onQuantityChange(quantity - 1)}
          >
            <LuMinus />
          </button>
          <span aria-live="polite">{quantity}</span>
          <button
            type="button"
            aria-label="Añadir una torta"
            disabled={quantity >= MAX_QUANTITY}
            onClick={() => onQuantityChange(quantity + 1)}
          >
            <LuPlus />
          </button>
        </div>

        <div className="cake-summary-amount">
          <span>Total</span>
          <strong>{money(unitPrice * quantity)}</strong>
          {quantity > 1 && <small>{money(unitPrice)} por torta</small>}
        </div>
      </div>

      <button type="button" className="cake-primary-button cake-add-button" onClick={onAddToCart} disabled={adding}>
        <LuShoppingBag aria-hidden="true" />
        {adding ? "Agregando..." : "Agregar al carrito"}
      </button>
    </div>
  );
}
