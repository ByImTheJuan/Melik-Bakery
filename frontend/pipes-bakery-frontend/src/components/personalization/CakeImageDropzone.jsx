import { useRef, useState } from "react";
import { LuImagePlus, LuLoaderCircle } from "react-icons/lu";
import { ACCEPTED_IMAGE_TYPES, validateImageFile } from "../../utils/productImage";

// Photo for the edible print. Same drag-and-drop behaviour as the admin product image field.
export default function CakeImageDropzone({ previewUrl, uploading, error, onSelect, onClear }) {
  const inputRef = useRef(null);
  const [isDragActive, setIsDragActive] = useState(false);
  const [validationError, setValidationError] = useState("");

  function selectFile(file) {
    if (!file) return;

    const message = validateImageFile(file);
    setValidationError(message ?? "");
    if (!message) {
      onSelect(file);
    }
  }

  function handleDrag(event) {
    event.preventDefault();
    event.dataTransfer.dropEffect = "copy";
    setIsDragActive(true);
  }

  function handleDragLeave(event) {
    event.preventDefault();
    setIsDragActive(false);
  }

  function handleDrop(event) {
    event.preventDefault();
    setIsDragActive(false);
    selectFile(event.dataTransfer.files?.[0]);
  }

  function handleKeyDown(event) {
    if (event.key === "Enter" || event.key === " ") {
      event.preventDefault();
      inputRef.current?.click();
    }
  }

  const shownError = validationError || error;

  return (
    <div className="cake-dropzone-field">
      <div
        className={`cake-dropzone${isDragActive ? " is-drag-active" : ""}${previewUrl ? " has-image" : ""}`}
        role="button"
        tabIndex={0}
        aria-label="Arrastra una foto o haz clic para seleccionarla"
        data-testid="cake-image-dropzone"
        onClick={() => inputRef.current?.click()}
        onKeyDown={handleKeyDown}
        onDragEnter={handleDrag}
        onDragOver={handleDrag}
        onDragLeave={handleDragLeave}
        onDrop={handleDrop}
      >
        {previewUrl ? (
          <img src={previewUrl} alt="Foto que se imprimirá sobre la torta" />
        ) : (
          <div className="cake-dropzone-placeholder">
            <LuImagePlus aria-hidden="true" />
            <strong>Arrastra tu foto aquí</strong>
            <small>o haz clic para elegirla · JPG, PNG o WEBP · máx. 5 MB</small>
          </div>
        )}

        {uploading && (
          <div className="cake-dropzone-uploading">
            <LuLoaderCircle aria-hidden="true" /> Subiendo foto...
          </div>
        )}

        <input
          ref={inputRef}
          type="file"
          accept={ACCEPTED_IMAGE_TYPES.join(",")}
          hidden
          data-testid="cake-image-input"
          onChange={(event) => {
            selectFile(event.target.files?.[0]);
            event.target.value = "";
          }}
        />
      </div>

      {previewUrl && !uploading && (
        <div className="cake-dropzone-actions">
          <button type="button" className="cake-link-button" onClick={() => inputRef.current?.click()}>
            Cambiar foto
          </button>
          <button type="button" className="cake-link-button" onClick={onClear}>
            Quitar foto
          </button>
        </div>
      )}

      {shownError && <p className="cake-field-error" role="alert">{shownError}</p>}
    </div>
  );
}
