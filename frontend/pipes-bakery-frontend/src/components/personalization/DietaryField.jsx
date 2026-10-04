import { useId } from "react";

// Small always-visible question before the size: does anyone have dietary restrictions?
export default function DietaryField({ design, dispatch, maxLength, showError, fieldRef }) {
  const questionId = useId();
  const detailId = useId();
  const answer = design.hasDietaryRestrictions;
  const missingAnswer = answer === null;
  const missingDetail = answer === true && !design.dietaryRestrictions.trim();

  return (
    <section className={`dietary-field${showError && (missingAnswer || missingDetail) ? " has-error" : ""}`} ref={fieldRef}>
      <div className="dietary-field-row">
        <p id={questionId} className="dietary-field-question">
          ¿Alguna restricción alimentaria?
        </p>
        <div className="dietary-field-options" role="radiogroup" aria-labelledby={questionId}>
          {[
            { value: false, label: "No" },
            { value: true, label: "Sí" },
          ].map((option) => (
            <button
              key={option.label}
              type="button"
              role="radio"
              aria-checked={answer === option.value}
              className={`dietary-option${answer === option.value ? " is-selected" : ""}`}
              onClick={() => dispatch({ type: "setDietaryAnswer", value: option.value })}
            >
              {option.label}
            </button>
          ))}
        </div>
      </div>

      {answer === true && (
        <label className="dietary-field-detail" htmlFor={detailId}>
          <span>¿Cuál? Cuéntanos para preparar la torta con cuidado.</span>
          <input
            id={detailId}
            type="text"
            value={design.dietaryRestrictions}
            maxLength={maxLength}
            placeholder="Ej.: sin gluten, alergia a las nueces"
            onChange={(event) => dispatch({ type: "setDietaryRestrictions", text: event.target.value })}
          />
        </label>
      )}

      {showError && missingAnswer && (
        <p className="cake-field-error" role="alert">Indícanos si hay alguna restricción alimentaria.</p>
      )}
      {showError && missingDetail && (
        <p className="cake-field-error" role="alert">Escribe qué restricción alimentaria debemos tener en cuenta.</p>
      )}
    </section>
  );
}
