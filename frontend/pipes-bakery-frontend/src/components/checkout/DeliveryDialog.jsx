import { useEffect, useId, useRef, useState } from "react";
import { LuX } from "react-icons/lu";
import DeliveryCalendar from "./DeliveryCalendar";
import {
  DELIVERY_SLOTS,
  MIN_DELIVERY_LEAD_DAYS,
  deliveryRange,
  formatDeliveryDate,
  isDeliveryComplete,
  isDeliveryDateAllowed,
} from "../../utils/delivery";

const FOCUSABLE = 'button:not([disabled]), [href], input, [tabindex]:not([tabindex="-1"])';

// Modal where the customer picks the delivery date and time slot. It closes by itself as soon
// as a valid date and slot are chosen. Without a confirmed delivery it can't be dismissed,
// because the order can't be placed without one (the customer can still go back to the cart).
export default function DeliveryDialog({ current, today, onConfirm, onClose, onBackToCart, notice, dismissible }) {
  const titleId = useId();
  const descriptionId = useId();
  const dialogRef = useRef(null);
  const titleRef = useRef(null);
  const [draft, setDraft] = useState({ date: current?.date ?? null, slot: current?.slot ?? null });
  const { min, max } = deliveryRange(today);
  const hasCurrent = isDeliveryComplete(current, today);
  // At checkout it can only be dismissed once a delivery exists; other screens may allow it anyway
  const canDismiss = dismissible ?? hasCurrent;
  const draftComplete = isDeliveryComplete(draft, today);

  useEffect(() => {
    const previouslyFocused = document.activeElement;
    const { overflow } = document.body.style;
    document.body.style.overflow = "hidden";
    titleRef.current?.focus();

    return () => {
      document.body.style.overflow = overflow;
      previouslyFocused?.focus?.();
    };
  }, []);

  function handleKeyDown(event) {
    if (event.key === "Escape" && canDismiss) {
      event.preventDefault();
      onClose();
      return;
    }

    // Keep keyboard focus inside the dialog
    if (event.key === "Tab") {
      const focusable = [...dialogRef.current.querySelectorAll(FOCUSABLE)];
      if (focusable.length === 0) return;
      const first = focusable[0];
      const last = focusable[focusable.length - 1];
      if (event.shiftKey && document.activeElement === first) {
        event.preventDefault();
        last.focus();
      } else if (!event.shiftKey && document.activeElement === last) {
        event.preventDefault();
        first.focus();
      }
    }
  }

  function selectDate(date) {
    const next = { ...draft, date };
    setDraft(next);
    // First time: date + slot completes the choice. When changing an existing delivery the
    // dialog stays open so the slot can be changed too ("Listo" confirms).
    if (!hasCurrent && isDeliveryComplete(next, today)) onConfirm(next);
  }

  function selectSlot(slot) {
    const next = { ...draft, slot };
    setDraft(next);
    if (isDeliveryComplete(next, today)) onConfirm(next);
  }

  return (
    <div
      className="delivery-dialog-backdrop"
      onMouseDown={(event) => {
        if (event.target === event.currentTarget && canDismiss) onClose();
      }}
    >
      <div
        ref={dialogRef}
        className="delivery-dialog"
        role="dialog"
        aria-modal="true"
        aria-labelledby={titleId}
        aria-describedby={descriptionId}
        onKeyDown={handleKeyDown}
      >
        {canDismiss && (
          <button type="button" className="delivery-dialog-close" aria-label="Cerrar" onClick={onClose}>
            <LuX aria-hidden="true" />
          </button>
        )}

        <h2 id={titleId} ref={titleRef} tabIndex={-1}>
          ¿Cuándo quieres recibir tu pedido?
        </h2>
        <p id={descriptionId} className="delivery-dialog-text">
          Elige el día y la franja de entrega. Horneamos cada pedido para ti, así que necesitamos al menos{" "}
          {MIN_DELIVERY_LEAD_DAYS} días para prepararlo.
        </p>

        {notice && (
          <p className="delivery-dialog-notice" role="alert">
            {notice}
          </p>
        )}

        <div className="delivery-dialog-body">
          <DeliveryCalendar selected={draft.date} min={min} max={max} today={today} onSelect={selectDate} />

          <div className="delivery-dialog-side">
            <fieldset className="delivery-slots">
              <legend>Franja horaria</legend>
              <div className="delivery-slot-options">
                {DELIVERY_SLOTS.map((slot) => (
                  <button
                    key={slot.id}
                    type="button"
                    className={`delivery-slot${draft.slot === slot.id ? " is-selected" : ""}`}
                    aria-pressed={draft.slot === slot.id}
                    onClick={() => selectSlot(slot.id)}
                  >
                    {slot.label}
                  </button>
                ))}
              </div>
            </fieldset>

            <p className="delivery-dialog-selection" aria-live="polite">
              {isDeliveryDateAllowed(draft.date, today)
                ? `Entrega el ${formatDeliveryDate(draft.date)}${draft.slot ? "" : " · elige la franja"}`
                : "Elige un día disponible en el calendario."}
            </p>

            <div className="delivery-dialog-actions">
              {!canDismiss && onBackToCart && (
                <button type="button" className="delivery-dialog-link" onClick={onBackToCart}>
                  Volver al carrito
                </button>
              )}
              {hasCurrent && draftComplete && (
                <button type="button" className="delivery-dialog-done" onClick={() => onConfirm(draft)}>
                  Listo
                </button>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
