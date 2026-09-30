import { fireEvent, render, screen } from "@testing-library/react";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import ImageDropzone from "./ImageDropzone";
import { MAX_IMAGE_SIZE_BYTES } from "../../utils/productImage";

function renderDropzone(props = {}) {
  const handlers = {
    onImageSelected: vi.fn(),
    onImageCleared: vi.fn(),
  };

  const utils = render(
    <ImageDropzone currentImageFile="" pendingImage={null} {...handlers} {...props} />
  );

  return { ...utils, ...handlers };
}

describe("ImageDropzone", () => {
  beforeEach(() => {
    vi.stubEnv("VITE_IMAGES_BASE_URL", "/api/images/");
    URL.createObjectURL = vi.fn(() => "blob:preview");
    URL.revokeObjectURL = vi.fn();
  });

  afterEach(() => {
    vi.unstubAllEnvs();
  });

  it("accepts a dropped image", () => {
    const image = new File(["png"], "tarta.png", { type: "image/png" });
    const { onImageSelected } = renderDropzone();

    fireEvent.drop(screen.getByTestId("image-dropzone"), { dataTransfer: { files: [image] } });

    expect(onImageSelected).toHaveBeenCalledWith(image);
  });

  it("accepts an image chosen from the file dialog", () => {
    const image = new File(["jpg"], "tarta.jpg", { type: "image/jpeg" });
    const { onImageSelected } = renderDropzone();

    fireEvent.change(screen.getByTestId("image-dropzone-input"), { target: { files: [image] } });

    expect(onImageSelected).toHaveBeenCalledWith(image);
  });

  it("rejects files that are not supported images", () => {
    const document = new File(["text"], "notes.txt", { type: "text/plain" });
    const { onImageSelected } = renderDropzone();

    fireEvent.drop(screen.getByTestId("image-dropzone"), { dataTransfer: { files: [document] } });

    expect(onImageSelected).not.toHaveBeenCalled();
    expect(screen.getByText("Formato no permitido. Usa una imagen JPG, PNG o WEBP.")).toBeInTheDocument();
  });

  it("rejects images larger than the maximum size", () => {
    const hugeImage = new File(["x"], "huge.png", { type: "image/png" });
    Object.defineProperty(hugeImage, "size", { value: MAX_IMAGE_SIZE_BYTES + 1 });
    const { onImageSelected } = renderDropzone();

    fireEvent.drop(screen.getByTestId("image-dropzone"), { dataTransfer: { files: [hugeImage] } });

    expect(onImageSelected).not.toHaveBeenCalled();
    expect(screen.getByText("La imagen supera el tamaño máximo de 5 MB.")).toBeInTheDocument();
  });

  it("shows the current product image when editing", () => {
    renderDropzone({ currentImageFile: "croissant.jpg" });

    expect(screen.getByAltText("Vista previa del producto")).toHaveAttribute("src", "/api/images/croissant.jpg");
    expect(screen.getByText("Imagen actual: croissant.jpg")).toBeInTheDocument();
  });

  it("previews a pending image and lets the admin remove it", () => {
    const image = new File(["png"], "tarta.png", { type: "image/png" });
    const { onImageCleared } = renderDropzone({ currentImageFile: "croissant.jpg", pendingImage: image });

    expect(screen.getByAltText("Vista previa del producto")).toHaveAttribute("src", "blob:preview");

    fireEvent.click(screen.getByRole("button", { name: "Quitar" }));

    expect(onImageCleared).toHaveBeenCalled();
  });

  it("does not let drag events bubble to the product list", () => {
    const parentDrop = vi.fn();
    const image = new File(["png"], "tarta.png", { type: "image/png" });

    render(
      <div onDrop={parentDrop}>
        <ImageDropzone currentImageFile="" pendingImage={null} onImageSelected={vi.fn()} onImageCleared={vi.fn()} />
      </div>
    );

    fireEvent.drop(screen.getByTestId("image-dropzone"), { dataTransfer: { files: [image] } });

    expect(parentDrop).not.toHaveBeenCalled();
  });
});
