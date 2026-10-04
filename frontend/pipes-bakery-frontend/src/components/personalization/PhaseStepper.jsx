import { LuCheck } from "react-icons/lu";

// The three phases of the personalization process; the current one is highlighted and
// completed ones can be revisited.
export default function PhaseStepper({ phases, current, onSelect }) {
  return (
    <nav className="phase-stepper" aria-label="Fases de la personalización">
      <ol>
        {phases.map((phase, index) => {
          const state = index === current ? "current" : index < current ? "done" : "upcoming";
          const content = (
            <>
              <span className="phase-stepper-marker" aria-hidden="true">
                {state === "done" ? <LuCheck /> : index + 1}
              </span>
              <span className="phase-stepper-label">{phase.label}</span>
            </>
          );

          return (
            <li key={phase.id} className={`phase-stepper-step is-${state}`}>
              {state === "done" ? (
                <button type="button" onClick={() => onSelect(index)}>
                  {content}
                </button>
              ) : (
                <span aria-current={state === "current" ? "step" : undefined}>{content}</span>
              )}
            </li>
          );
        })}
      </ol>
    </nav>
  );
}
