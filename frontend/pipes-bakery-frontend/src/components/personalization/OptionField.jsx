import { useId } from "react";
import { LuChevronDown } from "react-icons/lu";

// One field of the personalization panel: a header showing the current choice that expands
// to reveal the options when clicked.
export default function OptionField({ label, value, open, onToggle, children }) {
  const panelId = useId();

  return (
    <section className={`option-field${open ? " is-open" : ""}`}>
      <h3>
        <button type="button" aria-expanded={open} aria-controls={panelId} onClick={onToggle}>
          <span className="option-field-label">{label}</span>
          <span className="option-field-value">{value}</span>
          <LuChevronDown className="option-field-chevron" aria-hidden="true" />
        </button>
      </h3>
      <div id={panelId} className="option-field-body" hidden={!open}>
        {children}
      </div>
    </section>
  );
}
