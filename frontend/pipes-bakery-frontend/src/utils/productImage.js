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
