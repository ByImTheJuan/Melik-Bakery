import { useReducer } from "react";

export const PHASES = [
  { id: "base", label: "Tamaño y Sabor" },
  { id: "decoration", label: "Decoración" },
  { id: "summary", label: "Resumen" },
];

export const MAX_DECORATIVE_TIERS = 2;

export const INITIAL_DESIGN = {
  phase: 0,
  // null until the customer answers: allergies are too important to assume "no"
  hasDietaryRestrictions: null,
  dietaryRestrictions: "",
  sizeId: "M",
  flavourId: "vainilla",
  decorativeTiers: 0,
  colorId: "chantilly",
  text: "",
  imageFile: null,
  imagePreviewUrl: null,
  extraIds: [],
  notes: "",
};

export function cakeBuilderReducer(state, action) {
  switch (action.type) {
    case "setDietaryAnswer":
      return { ...state, hasDietaryRestrictions: action.value };
    case "setDietaryRestrictions":
      return { ...state, dietaryRestrictions: action.text };
    case "setSize":
      return { ...state, sizeId: action.sizeId };
    case "setFlavour":
      return { ...state, flavourId: action.flavourId };
    case "setDecorativeTiers":
      return { ...state, decorativeTiers: Math.max(0, Math.min(action.count, MAX_DECORATIVE_TIERS)) };
    case "setColor":
      return { ...state, colorId: action.colorId };
    case "setText":
      return { ...state, text: action.text };
    case "setImage":
      return { ...state, imageFile: action.imageFile, imagePreviewUrl: action.previewUrl };
    case "clearImage":
      return { ...state, imageFile: null, imagePreviewUrl: null };
    case "toggleExtra": {
      const selected = state.extraIds.includes(action.extraId);
      return {
        ...state,
        extraIds: selected
          ? state.extraIds.filter((id) => id !== action.extraId)
          : [...state.extraIds, action.extraId],
      };
    }
    case "setNotes":
      return { ...state, notes: action.notes };
    case "goToPhase":
      return { ...state, phase: Math.max(0, Math.min(action.phase, PHASES.length - 1)) };
    case "reset":
      return { ...INITIAL_DESIGN };
    default:
      return state;
  }
}

export function useCakeBuilder() {
  return useReducer(cakeBuilderReducer, INITIAL_DESIGN);
}
