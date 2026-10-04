import { LuCalendarDays } from "react-icons/lu";
import { formatDeliveryDate, slotLabel } from "../../utils/delivery";

// Always visible above the checkout form: the chosen delivery, and the way to change it.
export default function DeliveryField({ delivery, onOpen, missing }) {
  const chosen = Boolean(delivery?.date && delivery?.slot);

  return (
    <button
      type="button"
      className={`delivery-field${missing ? " is-missing" : ""}`}
      onClick={onOpen}
      aria-haspopup="dialog"
    >
      <LuCalendarDays className="delivery-field-icon" aria-hidden="true" />
      <span className="delivery-field-text">
        <span className="delivery-field-label">Entrega</span>
        <strong>
          {chosen
            ? `${formatDeliveryDate(delivery.date)} · ${slotLabel(delivery.slot)}`
            : "Elige la fecha y la franja de entrega"}
        </strong>
      </span>
      <span className="delivery-field-action">{chosen ? "Cambiar" : "Elegir"}</span>
    </button>
  );
}
