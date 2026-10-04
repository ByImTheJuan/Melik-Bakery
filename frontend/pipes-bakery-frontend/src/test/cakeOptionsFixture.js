// Same shape and values as GET /api/custom-cakes/options (CustomCakeCatalog on the backend).
export const CAKE_OPTIONS = {
  sizes: [
    { id: "S", label: "Pequeña", description: "15 cm", servings: 8, basePrice: 95000, tierDiameters: [15] },
    { id: "M", label: "Mediana", description: "20 cm", servings: 16, basePrice: 140000, tierDiameters: [20] },
    {
      id: "L",
      label: "Grande",
      description: "2 pisos de 20 y 15 cm",
      servings: 24,
      basePrice: 230000,
      tierDiameters: [20, 15],
    },
  ],
  flavours: [
    { id: "vainilla", label: "Vainilla", price: 0, spongeColor: "#f3dca2", fillingColor: "#fff6e3" },
    { id: "chocolate", label: "Chocolate", price: 0, spongeColor: "#5b3420", fillingColor: "#3a2014" },
    { id: "red-velvet", label: "Red velvet", price: 10000, spongeColor: "#9e2a2b", fillingColor: "#fbf4ea" },
  ],
  colors: [
    { id: "chantilly", label: "Crema chantilly", hex: "#fbf5ea" },
    { id: "fresa", label: "Rosa fresa", hex: "#eebcb8" },
  ],
  extras: [
    { id: "chispas", label: "Chispas", price: 6000 },
    { id: "velas", label: "Velas", price: 5000 },
  ],
  maxDecorativeTiers: 2,
  decorativeTierPrice: 35000,
  maxTextLength: 40,
  textPrice: 8000,
  imagePrice: 18000,
  maxNotesLength: 500,
  maxDietaryLength: 200,
};
