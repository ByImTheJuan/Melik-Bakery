import apiClient from "../api/apiClient";

export async function getCustomCakeOptions() {
  const response = await apiClient.get("/custom-cakes/options");
  return response.data;
}

// Uploads the customer's photo for the edible print; returns its relative path
// (e.g. "custom-cakes/cake-ab12.jpg") to send as the cake's imageFile.
export async function uploadCakeImage(file) {
  const formData = new FormData();
  formData.append("file", file);

  const response = await apiClient.post("/custom-cakes/images", formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
  return response.data.imageFile;
}
