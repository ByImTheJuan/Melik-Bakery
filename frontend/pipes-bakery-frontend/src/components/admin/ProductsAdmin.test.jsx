import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import ProductsAdmin from "./ProductsAdmin";
import * as productService from "../../services/productService";

vi.mock("../../services/productService", () => ({
  getAllProducts: vi.fn(),
  createProduct: vi.fn(),
  updateProduct: vi.fn(),
  deleteProduct: vi.fn(),
  updateProductOrder: vi.fn(),
}));

const products = [
  {
    id: 1,
    name: "Croissant",
    description: "Mantequilla",
    price: 9500,
    ingredients: ["Harina", "Mantequilla"],
    imageUrl: "/images/products/croissant.jpg",
  },
];

describe("ProductsAdmin", () => {
  beforeEach(() => {
    vi.stubEnv("VITE_IMAGES_BASE_URL", "https://cdn.melik.test");
    productService.getAllProducts.mockReset();
    productService.createProduct.mockReset();
    productService.updateProduct.mockReset();
    productService.deleteProduct.mockReset();
    productService.updateProductOrder.mockReset();
  });

  it("loads products and creates a new one with dynamic ingredients", async () => {
    productService.getAllProducts.mockResolvedValue(products);
    productService.createProduct.mockResolvedValue({ id: 2 });

    render(<ProductsAdmin />);

    expect(await screen.findByText("Croissant")).toBeInTheDocument();

    fireEvent.change(screen.getByLabelText("Nombre"), {
      target: { value: "Baguette" },
    });
    fireEvent.change(screen.getByLabelText("Descripción"), {
      target: { value: "Corteza crujiente" },
    });
    fireEvent.change(screen.getByLabelText("Precio"), {
      target: { value: "7000" },
    });

    const ingredientInputs = screen.getAllByRole("textbox").filter((input) =>
      input.closest(".products-admin-ingredient-row")
    );
    fireEvent.change(ingredientInputs[0], {
      target: { value: "Harina" },
    });
    fireEvent.click(screen.getByRole("button", { name: "Añadir ingrediente" }));

    const updatedIngredientInputs = screen.getAllByRole("textbox").filter((input) =>
      input.closest(".products-admin-ingredient-row")
    );
    fireEvent.change(updatedIngredientInputs[1], {
      target: { value: "Agua" },
    });

    fireEvent.change(screen.getByPlaceholderText("/images/products/nombre-del-archivo.jpg"), {
      target: { value: "/images/products/baguette.jpg" },
    });

    fireEvent.click(screen.getByRole("button", { name: "Crear producto" }));

    await waitFor(() => {
      expect(productService.createProduct).toHaveBeenCalledWith({
        name: "Baguette",
        description: "Corteza crujiente",
        price: 7000,
        ingredients: ["Harina", "Agua"],
        imageUrl: "/images/products/baguette.jpg",
      });
    });
  });

  it("switches to edit mode and updates an existing product", async () => {
    productService.getAllProducts.mockResolvedValue(products);
    productService.updateProduct.mockResolvedValue({ ...products[0], name: "Croissant premium" });

    render(<ProductsAdmin />);

    fireEvent.click(await screen.findByRole("button", { name: "Editar" }));

    fireEvent.change(screen.getByLabelText("Nombre"), {
      target: { value: "Croissant premium" },
    });
    fireEvent.click(screen.getByRole("button", { name: "Guardar cambios" }));

    await waitFor(() => {
      expect(productService.updateProduct).toHaveBeenCalledWith(1, {
        name: "Croissant premium",
        description: "Mantequilla",
        price: 9500,
        ingredients: ["Harina", "Mantequilla"],
        imageUrl: "/images/products/croissant.jpg",
      });
    });
  });

  it("persists a new product order when products are dragged", async () => {
    const sortableProducts = [
      products[0],
      {
        id: 2,
        name: "Baguette",
        description: "Corteza crujiente",
        price: 7000,
        ingredients: ["Harina", "Agua"],
        imageUrl: "/images/products/baguette.jpg",
      },
    ];
    const dataTransfer = {
      data: {},
      dropEffect: "",
      effectAllowed: "",
      getData(type) {
        return this.data[type];
      },
      setData(type, value) {
        this.data[type] = value;
      },
    };

    productService.getAllProducts.mockResolvedValue(sortableProducts);
    productService.updateProductOrder.mockResolvedValue([
      sortableProducts[1],
      sortableProducts[0],
    ]);

    render(<ProductsAdmin />);

    const croissant = await screen.findByText("Croissant");
    const baguette = await screen.findByText("Baguette");

    fireEvent.dragStart(croissant.closest(".products-admin-item"), { dataTransfer });
    fireEvent.dragOver(baguette.closest(".products-admin-item"), { dataTransfer });
    fireEvent.drop(baguette.closest(".products-admin-item"), { dataTransfer });

    await waitFor(() => {
      expect(productService.updateProductOrder).toHaveBeenCalledWith([2, 1]);
    });
    expect(screen.getByText("Orden del catálogo actualizado.")).toBeInTheDocument();
  });
});
