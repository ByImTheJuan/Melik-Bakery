import { render, screen, waitFor, within } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { MemoryRouter } from "react-router-dom";
import PersonalizationPage from "./PersonalizationPage";
import { useCart } from "../hooks/useCart";
import { getCustomCakeOptions, uploadCakeImage } from "../services/customCakeService";
import { CAKE_OPTIONS } from "../test/cakeOptionsFixture";

const navigateMock = vi.fn();
const addCustomCakeMock = vi.fn();

vi.mock("react-router-dom", async () => {
  const actual = await vi.importActual("react-router-dom");
  return { ...actual, useNavigate: () => navigateMock };
});

vi.mock("../hooks/useCart", () => ({ useCart: vi.fn() }));

vi.mock("../services/customCakeService", () => ({
  getCustomCakeOptions: vi.fn(),
  uploadCakeImage: vi.fn(),
}));

vi.mock("react-toastify", () => ({ toast: { success: vi.fn(), error: vi.fn() } }));

// jsdom has no WebGL: replace the 3D viewer with a probe that exposes what it would draw
vi.mock("../components/personalization/CakeViewer", () => ({
  default: ({ cake, caption }) => (
    <div
      data-testid="cake-viewer"
      data-tiers={cake.tierDiameters.join("-")}
      data-decorative={cake.decorativeTiers}
      data-frosting={cake.frostingHex}
    >
      {caption}
    </div>
  ),
}));

function renderPage() {
  return render(
    <MemoryRouter>
      <PersonalizationPage />
    </MemoryRouter>
  );
}

async function answerDietary(user, answer) {
  const group = screen.getByRole("radiogroup", { name: "¿Alguna restricción alimentaria?" });
  await user.click(within(group).getByRole("radio", { name: answer }));
}

describe("PersonalizationPage", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    getCustomCakeOptions.mockResolvedValue(CAKE_OPTIONS);
    addCustomCakeMock.mockResolvedValue(undefined);
    useCart.mockReturnValue({ cartId: "cart-1", cart: { cartId: "cart-1" }, addCustomCake: addCustomCakeMock });
    URL.createObjectURL = vi.fn(() => "blob:preview");
    URL.revokeObjectURL = vi.fn();
    Element.prototype.scrollIntoView = vi.fn();
  });

  it("asks about dietary restrictions first, then Tamaño, Sabor and Pisos", async () => {
    renderPage();

    const panel = await screen.findByRole("region", { name: "Tamaño y Sabor" });
    const question = within(panel).getByText("¿Alguna restricción alimentaria?");
    const fields = within(panel).getAllByRole("heading", { level: 3 });

    expect(fields.map((heading) => heading.textContent)).toEqual([
      expect.stringMatching(/^Tamaño/),
      expect.stringMatching(/^Sabor/),
      expect.stringMatching(/^Pisos/),
    ]);
    expect(question.compareDocumentPosition(fields[0]) & Node.DOCUMENT_POSITION_FOLLOWING).toBeTruthy();

    const steps = screen.getByRole("navigation", { name: "Fases de la personalización" });
    expect(within(steps).getByText("Tamaño y Sabor").closest("[aria-current]")).toHaveAttribute("aria-current", "step");
  });

  it("offers three sizes and draws the two real tiers of the Grande cake", async () => {
    const user = userEvent.setup();
    renderPage();
    await screen.findByTestId("cake-viewer");

    const sizes = screen.getByRole("radiogroup", { name: "Tamaño" });
    expect(within(sizes).getAllByRole("radio")).toHaveLength(3);
    expect(within(sizes).getByRole("radio", { name: /Pequeña.*15 cm.*8 personas/ })).toBeInTheDocument();
    expect(within(sizes).getByRole("radio", { name: /Mediana.*20 cm.*16 personas/ })).toBeInTheDocument();

    const large = within(sizes).getByRole("radio", { name: /Grande.*2 pisos de 20 y 15 cm.*24 personas/ });
    expect(large).not.toHaveTextContent("40 cm");
    expect(large.querySelectorAll(".cake-glyph span")).toHaveLength(2);

    await user.click(large);
    expect(screen.getByTestId("cake-viewer")).toHaveAttribute("data-tiers", "20-15");
  });

  it("explains that Pisos are only decorative and lets the customer add none, one or two", async () => {
    const user = userEvent.setup();
    renderPage();
    await screen.findByTestId("cake-viewer");

    await user.click(screen.getByRole("button", { name: /Pisos/ }));

    expect(screen.getByText(/solo decorativos/)).toBeInTheDocument();
    const options = screen.getByRole("radiogroup", { name: "Pisos decorativos" });
    expect(within(options).getAllByRole("radio").map((radio) => radio.textContent)).toEqual([
      expect.stringMatching(/^Ninguno/),
      expect.stringMatching(/^1 piso decorativo/),
      expect.stringMatching(/^2 pisos decorativos/),
    ]);

    await user.click(within(options).getByRole("radio", { name: /2 pisos decorativos/ }));
    // The real cake doesn't change, only the decoration underneath it
    expect(screen.getByTestId("cake-viewer")).toHaveAttribute("data-decorative", "2");
    expect(screen.getByTestId("cake-viewer")).toHaveAttribute("data-tiers", "20");
  });

  it("doesn't continue until the dietary question is answered and the restriction is named", async () => {
    const user = userEvent.setup();
    renderPage();
    await screen.findByTestId("cake-viewer");

    await user.click(screen.getByRole("button", { name: /Continuar a decoración/ }));
    expect(screen.getByRole("alert")).toHaveTextContent("Indícanos si hay alguna restricción alimentaria");
    expect(screen.getByRole("region", { name: "Tamaño y Sabor" })).toBeInTheDocument();

    await answerDietary(user, "Sí");
    await user.click(screen.getByRole("button", { name: /Continuar a decoración/ }));
    expect(screen.getByRole("alert")).toHaveTextContent("Escribe qué restricción alimentaria");

    await user.type(screen.getByPlaceholderText(/sin gluten/), "Sin gluten");
    await user.click(screen.getByRole("button", { name: /Continuar a decoración/ }));
    expect(screen.getByRole("region", { name: "Decoración" })).toBeInTheDocument();
  });

  it("keeps the viewer while moving through the phases and adds the cake with notes to the cart", async () => {
    const user = userEvent.setup();
    renderPage();
    const viewer = await screen.findByTestId("cake-viewer");

    await answerDietary(user, "Sí");
    await user.type(screen.getByPlaceholderText(/sin gluten/), "Alergia a las nueces");
    await user.click(screen.getByRole("button", { name: /Continuar a decoración/ }));
    expect(screen.getByRole("region", { name: "Decoración" })).toBeInTheDocument();
    expect(screen.getByTestId("cake-viewer")).toBe(viewer);

    await user.click(screen.getByRole("radio", { name: /Rosa fresa/ }));
    expect(viewer).toHaveAttribute("data-frosting", "#eebcb8");

    await user.click(screen.getByRole("button", { name: /Texto/ }));
    await user.type(screen.getByRole("textbox"), "Feliz día");

    await user.click(screen.getByRole("button", { name: /Otros elementos decorativos/ }));
    await user.click(screen.getByRole("button", { name: /Velas/ }));

    await user.click(screen.getByRole("button", { name: /Ver resumen/ }));
    expect(screen.getByRole("region", { name: "Resumen" })).toBeInTheDocument();
    expect(screen.getByText("Alergia a las nueces")).toBeInTheDocument();
    // 140000 + text 8000 + velas 5000
    expect(screen.getByText("$153.000")).toBeInTheDocument();

    await user.type(screen.getByLabelText(/Notas para el pastelero/), "Es una sorpresa");
    await user.click(screen.getByRole("button", { name: /Agregar al carrito/ }));

    await waitFor(() =>
      expect(addCustomCakeMock).toHaveBeenCalledWith(
        {
          hasDietaryRestrictions: true,
          dietaryRestrictions: "Alergia a las nueces",
          sizeId: "M",
          flavourId: "vainilla",
          decorativeTiers: 0,
          colorId: "fresa",
          text: "Feliz día",
          imageFile: null,
          extraIds: ["velas"],
          notes: "Es una sorpresa",
        },
        1
      )
    );
    expect(await screen.findByText("Tu torta está en el carrito")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Ver carrito" }));
    expect(navigateMock).toHaveBeenCalledWith("/cart/cart-1");
  });

  it("uploads the photo for the edible print", async () => {
    const user = userEvent.setup();
    uploadCakeImage.mockResolvedValue("custom-cakes/cake-1.jpg");
    renderPage();
    await screen.findByTestId("cake-viewer");

    await answerDietary(user, "No");
    await user.click(screen.getByRole("button", { name: /Continuar a decoración/ }));
    await user.click(screen.getByRole("button", { name: /Imagen/ }));

    const file = new File(["img"], "foto.jpg", { type: "image/jpeg" });
    await user.upload(screen.getByTestId("cake-image-input"), file);

    expect(uploadCakeImage).toHaveBeenCalledWith(file);
    expect(await screen.findByAltText("Foto que se imprimirá sobre la torta")).toHaveAttribute("src", "blob:preview");
  });

  it("offers a retry when the options can't be loaded", async () => {
    getCustomCakeOptions.mockRejectedValueOnce(new Error("offline"));
    const user = userEvent.setup();
    renderPage();

    await user.click(await screen.findByRole("button", { name: "Reintentar" }));

    expect(await screen.findByTestId("cake-viewer")).toBeInTheDocument();
    expect(getCustomCakeOptions).toHaveBeenCalledTimes(2);
  });
});
