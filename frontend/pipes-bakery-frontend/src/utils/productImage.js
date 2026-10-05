// Builds the public URL of a product image from the stored file name.
// The backend stores only the file name (e.g. "cinnamonRoll.jpg"); the base URL
// comes from VITE_IMAGES_BASE_URL. Slashes are normalized so that a base with or
// without a trailing "/" and legacy values with a leading "/" all resolve.
export function getProductImageUrl(fileName) {
  if (!fileName) {
    return "";
  }

  const baseUrl = import.meta.env.VITE_IMAGES_BASE_URL ?? "";
  const normalizedBase = baseUrl.endsWith("/") ? baseUrl : `${baseUrl}/`;
  const normalizedFile = fileName.replace(/^\/+/, "");
  // Custom cake photos live in a subfolder ("custom-cakes/x.jpg"): encode each segment, keep the "/"
  const encodedPath = normalizedFile.split("/").map(encodeURIComponent).join("/");

  return `${normalizedBase}${encodedPath}`;
}

export const ACCEPTED_IMAGE_TYPES = ["image/jpeg", "image/png", "image/webp"];
export const MAX_IMAGE_SIZE_BYTES = 5 * 1024 * 1024;

// Client-side check mirroring the backend rules; returns an error message or null.
export function validateImageFile(file) {
  if (!ACCEPTED_IMAGE_TYPES.includes(file.type)) {
    return "Formato no permitido. Usa una imagen JPG, PNG o WEBP.";
  }

  if (file.size > MAX_IMAGE_SIZE_BYTES) {
    return "La imagen supera el tamaño máximo de 5 MB.";
  }

  return null;
}

const UPLOAD_MAX_DIMENSION = 1200;
const UPLOAD_WEBP_QUALITY = 0.8;

// Downscales and re-encodes a product photo as WebP before it is uploaded, so the
// catalog serves ~100 KB images instead of multi-megabyte camera files. Falls back to
// the original file whenever the browser can't do it or the result isn't smaller.
export async function compressImageForUpload(file) {
  if (typeof createImageBitmap !== "function") {
    return file;
  }

  try {
    const bitmap = await createImageBitmap(file);
    const scale = Math.min(1, UPLOAD_MAX_DIMENSION / Math.max(bitmap.width, bitmap.height));
    const canvas = document.createElement("canvas");
    canvas.width = Math.round(bitmap.width * scale);
    canvas.height = Math.round(bitmap.height * scale);
    canvas.getContext("2d").drawImage(bitmap, 0, 0, canvas.width, canvas.height);
    bitmap.close();

    const blob = await new Promise((resolve) =>
      canvas.toBlob(resolve, "image/webp", UPLOAD_WEBP_QUALITY)
    );

    // Browsers without WebP encoding hand back a PNG instead
    if (!blob || blob.type !== "image/webp" || blob.size >= file.size) {
      return file;
    }

    const baseName = file.name.replace(/\.[^.]+$/, "");
    return new File([blob], `${baseName}.webp`, { type: "image/webp" });
  } catch {
    return file;
  }
}
