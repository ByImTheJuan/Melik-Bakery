import { useState } from "react";
import { LuChevronLeft, LuChevronRight } from "react-icons/lu";
import { addMonths, formatDeliveryDate, formatMonth, monthGrid } from "../../utils/delivery";

const WEEKDAYS = ["lu", "ma", "mi", "ju", "vi", "sá", "do"];

// Month calendar where only dates inside [min, max] can be picked.
export default function DeliveryCalendar({ selected, min, max, today, onSelect }) {
  const [visibleMonth, setVisibleMonth] = useState(() => (selected ?? min).slice(0, 7));
  const weeks = monthGrid(`${visibleMonth}-01`);
  const canGoBack = visibleMonth > min.slice(0, 7);
  const canGoForward = visibleMonth < max.slice(0, 7);

  return (
    <div className="delivery-calendar">
      <div className="delivery-calendar-header">
        <button
          type="button"
          className="delivery-calendar-nav"
          aria-label="Mes anterior"
          disabled={!canGoBack}
          onClick={() => setVisibleMonth(addMonths(`${visibleMonth}-01`, -1).slice(0, 7))}
        >
          <LuChevronLeft aria-hidden="true" />
        </button>
        <span className="delivery-calendar-month" aria-live="polite">
          {formatMonth(`${visibleMonth}-01`)}
        </span>
        <button
          type="button"
          className="delivery-calendar-nav"
          aria-label="Mes siguiente"
          disabled={!canGoForward}
          onClick={() => setVisibleMonth(addMonths(`${visibleMonth}-01`, 1).slice(0, 7))}
        >
          <LuChevronRight aria-hidden="true" />
        </button>
      </div>

      <table className="delivery-calendar-grid">
        <thead>
          <tr>
            {WEEKDAYS.map((day) => (
              <th key={day} scope="col">{day}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {weeks.map((week, index) => (
            <tr key={index}>
              {week.map((date, dayIndex) => {
                if (!date) return <td key={dayIndex} />;

                const available = date >= min && date <= max;
                const isSelected = date === selected;
                return (
                  <td key={date}>
                    <button
                      type="button"
                      className={`delivery-day${isSelected ? " is-selected" : ""}${date === today ? " is-today" : ""}`}
                      disabled={!available}
                      aria-pressed={isSelected}
                      aria-label={`${formatDeliveryDate(date)}${available ? "" : " (no disponible)"}`}
                      onClick={() => onSelect(date)}
                    >
                      {Number(date.slice(8))}
                    </button>
                  </td>
                );
              })}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
