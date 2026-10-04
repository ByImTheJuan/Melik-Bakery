// Delivery date rules, mirrored from OrderService on the backend (which is the one that enforces them).
// Dates are handled as "YYYY-MM-DD" strings so no time zone can shift them by a day.

export const BAKERY_TIME_ZONE = "America/Bogota";
export const MIN_DELIVERY_LEAD_DAYS = 4;
export const MAX_DELIVERY_LEAD_DAYS = 90;

export const DELIVERY_SLOTS = [
  { id: "MORNING", label: "Mañana" },
  { id: "AFTERNOON", label: "Tarde" },
];

// "en la mañana" / "en la tarde", for sentences
export function slotPhrase(slotId) {
  const label = slotLabel(slotId);
  return label ? `en la ${label.toLowerCase()}` : "";
}

export function slotLabel(slotId) {
  return DELIVERY_SLOTS.find((slot) => slot.id === slotId)?.label ?? "";
}

// Today's date in Bogota, whatever the customer's device time zone is.
export function bakeryToday(now = new Date()) {
  const parts = new Intl.DateTimeFormat("en-CA", {
    timeZone: BAKERY_TIME_ZONE,
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  }).formatToParts(now);
  const get = (type) => parts.find((part) => part.type === type).value;
  return `${get("year")}-${get("month")}-${get("day")}`;
}

function toUtcDate(isoDate) {
  const [year, month, day] = isoDate.split("-").map(Number);
  return new Date(Date.UTC(year, month - 1, day));
}

export function toIsoDate(date) {
  return date.toISOString().slice(0, 10);
}

export function addDays(isoDate, days) {
  const date = toUtcDate(isoDate);
  date.setUTCDate(date.getUTCDate() + days);
  return toIsoDate(date);
}

export function deliveryRange(today = bakeryToday()) {
  return {
    min: addDays(today, MIN_DELIVERY_LEAD_DAYS),
    max: addDays(today, MAX_DELIVERY_LEAD_DAYS),
  };
}

export function isDeliveryDateAllowed(isoDate, today = bakeryToday()) {
  if (!isoDate) return false;
  const { min, max } = deliveryRange(today);
  return isoDate >= min && isoDate <= max;
}

export function isDeliveryComplete(delivery, today = bakeryToday()) {
  return isDeliveryDateAllowed(delivery?.date, today) && Boolean(slotLabel(delivery?.slot));
}

// "sábado, 10 de octubre de 2026"
export function formatDeliveryDate(isoDate, { withYear = true } = {}) {
  if (!isoDate) return "";
  return new Intl.DateTimeFormat("es-CO", {
    weekday: "long",
    day: "numeric",
    month: "long",
    ...(withYear ? { year: "numeric" } : {}),
    timeZone: "UTC",
  }).format(toUtcDate(isoDate));
}

// Weeks (Monday first) of the month containing isoDate; days outside the month are null.
export function monthGrid(isoMonthDate) {
  const first = toUtcDate(`${isoMonthDate.slice(0, 7)}-01`);
  const month = first.getUTCMonth();
  const leadingBlanks = (first.getUTCDay() + 6) % 7;
  const cells = Array.from({ length: leadingBlanks }, () => null);

  const cursor = new Date(first);
  while (cursor.getUTCMonth() === month) {
    cells.push(toIsoDate(cursor));
    cursor.setUTCDate(cursor.getUTCDate() + 1);
  }
  while (cells.length % 7 !== 0) cells.push(null);

  return Array.from({ length: cells.length / 7 }, (_, week) => cells.slice(week * 7, week * 7 + 7));
}

export function addMonths(isoDate, months) {
  const date = toUtcDate(`${isoDate.slice(0, 7)}-01`);
  date.setUTCMonth(date.getUTCMonth() + months);
  return toIsoDate(date);
}

// "Octubre de 2026"
export function formatMonth(isoDate) {
  const label = new Intl.DateTimeFormat("es-CO", { month: "long", year: "numeric", timeZone: "UTC" }).format(
    toUtcDate(`${isoDate.slice(0, 7)}-01`)
  );
  return label.charAt(0).toUpperCase() + label.slice(1);
}
