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
  uploadProductImage: vi.fn(),
}));

const products = [
  {
    id: 1,
    name: "Croissant",
    description: "Mantequilla",
    price: 9500,
    ingredients: ["Harina", "Mantequilla"],
    imageFile: "croissant.jpg",
  },
];

describe("ProductsAdmin", () => {
  beforeEach(() => {
    vi.stubEnv("VITE_IMAGES_BASE_URL", "https://cdn.melik.test/images/");
    productService.getAllProducts.mockReset();
    productService.createProduct.mockReset();
    productService.updateProduct.mockReset();
    productService.deleteProduct.mockReset();
    productService.updateProductOrder.mockReset();
    productService.uploadProductImage.mockReset();
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
        imageFile: "croissant.jpg",
      });
    });
    expect(productService.uploadProductImage).not.toHaveBeenCalled();
  });

  it("shows the backend validation detail when an update is rejected", async () => {
    productService.getAllProducts.mockResolvedValue(products);
    productService.updateProduct.mockRejectedValue({
      response: {
        data: {
          message: "Validation failed",
          details: ["Price must be positive"],
        },
      },
    });

    render(<ProductsAdmin />);

    fireEvent.click(await screen.findByRole("button", { name: "Editar" }));
    fireEvent.click(screen.getByRole("button", { name: "Guardar cambios" }));

    expect(await screen.findByText("Price must be positive")).toBeInTheDocument();
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
        imageFile: "baguette.jpg",
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

  it("builds image URLs from the stored file name", async () => {
    productService.getAllProducts.mockResolvedValue(products);

    render(<ProductsAdmin />);

    expect(await screen.findByAltText("Croissant")).toHaveAttribute(
      "src",
      "https://cdn.melik.test/images/croissant.jpg"
    );
  });

  it("uploads a dropped image before creating the product", async () => {
    const image = new File(["png"], "tarta.png", { type: "image/png" });
    URL.createObjectURL = vi.fn(() => "blob:preview");
    URL.revokeObjectURL = vi.fn();
    productService.getAllProducts.mockResolvedValue([]);
    productService.uploadProductImage.mockResolvedValue({ imageFile: "tarta-1a2b3c4d.png" });
    productService.createProduct.mockResolvedValue({ id: 3 });

    render(<ProductsAdmin />);

    fireEvent.change(await screen.findByLabelText("Nombre"), { target: { value: "Tarta" } });
    fireEvent.change(screen.getByLabelText("Precio"), { target: { value: "12000" } });
    fireEvent.change(screen.getByRole("textbox", { name: "" }), { target: { value: "Harina" } });
    fireEvent.drop(screen.getByTestId("image-dropzone"), { dataTransfer: { files: [image] } });

    expect(await screen.findByText("Nueva imagen: tarta.png")).toBeInTheDocument();

    fireEvent.click(screen.getByRole("button", { name: "Crear producto" }));

    await waitFor(() => {
      expect(productService.createProduct).toHaveBeenCalledWith({
        name: "Tarta",
        description: "",
        price: 12000,
        ingredients: ["Harina"],
        imageFile: "tarta-1a2b3c4d.png",
      });
    });
    expect(productService.uploadProductImage).toHaveBeenCalledWith(image);
  });

  it("requires an image when creating a product", async () => {
    productService.getAllProducts.mockResolvedValue([]);

    render(<ProductsAdmin />);

    fireEvent.change(await screen.findByLabelText("Nombre"), { target: { value: "Tarta" } });
    fireEvent.change(screen.getByLabelText("Precio"), { target: { value: "12000" } });
    fireEvent.change(screen.getByRole("textbox", { name: "" }), { target: { value: "Harina" } });
    fireEvent.click(screen.getByRole("button", { name: "Crear producto" }));

    expect(await screen.findByText("Añade una imagen del producto.")).toBeInTheDocument();
    expect(productService.createProduct).not.toHaveBeenCalled();
  });
});
