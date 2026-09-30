import { useEffect, useRef, useState } from "react";
import {
  ACCEPTED_IMAGE_TYPES,
  getProductImageUrl,
  validateImageFile,
} from "../../utils/productImage";

export default function ImageDropzone({ currentImageFile, pendingImage, onImageSelected, onImageCleared }) {
  const inputRef = useRef(null);
  const [isDragActive, setIsDragActive] = useState(false);
  const [validationError, setValidationError] = useState("");
  const [previewUrl, setPreviewUrl] = useState("");

  useEffect(() => {
    if (!pendingImage) {
      setPreviewUrl("");
      return undefined;
    }

    const objectUrl = URL.createObjectURL(pendingImage);
    setPreviewUrl(objectUrl);

    return () => URL.revokeObjectURL(objectUrl);
  }, [pendingImage]);

  const displayedImage = previewUrl || getProductImageUrl(currentImageFile);

  function selectFile(file) {
    if (!file) {
      return;
    }

    const error = validateImageFile(file);
    setValidationError(error ?? "");

    if (!error) {
      onImageSelected(file);
    }
  }

  // stopPropagation keeps these events away from the product list's reorder drag-and-drop.
  function handleDragOver(event) {
    event.preventDefault();
    event.stopPropagation();
    event.dataTransfer.dropEffect = "copy";
    setIsDragActive(true);
  }

  function handleDragLeave(event) {
    event.preventDefault();
    event.stopPropagation();
    setIsDragActive(false);
  }

  function handleDrop(event) {
    event.preventDefault();
    event.stopPropagation();
    setIsDragActive(false);
    selectFile(event.dataTransfer.files?.[0]);
  }

  function handleInputChange(event) {
    selectFile(event.target.files?.[0]);
    // Allow picking the same file again after clearing it.
    event.target.value = "";
  }

  function openFileDialog() {
    inputRef.current?.click();
  }

  function handleKeyDown(event) {
    if (event.key === "Enter" || event.key === " ") {
      event.preventDefault();
      openFileDialog();
    }
  }

  function handleClear(event) {
    event.stopPropagation();
    setValidationError("");
    onImageCleared();
  }

  return (
    <div className="products-admin-image-field">
      <span>Imagen del producto</span>

      <div
        className={`products-admin-dropzone${isDragActive ? " is-drag-active" : ""}${
          displayedImage ? " has-image" : ""
        }`}
        role="button"
        tabIndex={0}
        aria-label="Arrastra una imagen o haz clic para seleccionarla"
        data-testid="image-dropzone"
        onClick={openFileDialog}
        onKeyDown={handleKeyDown}
        onDragEnter={handleDragOver}
        onDragOver={handleDragOver}
        onDragLeave={handleDragLeave}
        onDrop={handleDrop}
      >
        {displayedImage ? (
          <img className="products-admin-dropzone-preview" src={displayedImage} alt="Vista previa del producto" />
        ) : (
          <div className="products-admin-dropzone-placeholder">
            <strong>Arrastra una imagen aquí</strong>
            <small>o haz clic para seleccionarla · JPG, PNG o WEBP · máx. 5 MB</small>
          </div>
        )}

        <input
          ref={inputRef}
          type="file"
          accept={ACCEPTED_IMAGE_TYPES.join(",")}
          onChange={handleInputChange}
          hidden
          data-testid="image-dropzone-input"
        />
      </div>

      {(pendingImage || currentImageFile) && (
        <div className="products-admin-dropzone-footer">
          <small>{pendingImage ? `Nueva imagen: ${pendingImage.name}` : `Imagen actual: ${currentImageFile}`}</small>
          <div className="products-admin-dropzone-actions">
            <button type="button" className="products-admin-secondary" onClick={openFileDialog}>
              Cambiar
            </button>
            {pendingImage && (
              <button type="button" className="products-admin-danger" onClick={handleClear}>
                Quitar
              </button>
            )}
          </div>
        </div>
      )}

      {validationError && <p className="products-admin-dropzone-error">{validationError}</p>}
    </div>
  );
}
