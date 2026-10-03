import apiClient from "../api/apiClient";

export const getAllProducts = async () => {
  const response = await apiClient.get("/products");
  return response.data;
};

export const getProductById = async (id) => {
  const response = await apiClient.get(`/products/${id}`);
  return await response.data;
};

export const createProduct = async (productData) => {
  const response = await apiClient.post("/products", productData);
  return response.data;
};

export const uploadProductImage = async (file) => {
  const formData = new FormData();
  formData.append("file", file);

  // Override the client's JSON default so axios sends real multipart data
  // (the browser adds the boundary to the header).
  const response = await apiClient.post("/products/images", formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
  return response.data;
};

export const updateProduct = async (id, productData) => {
  const response = await apiClient.put(`/products/${id}`, productData);
  return response.data;
};

export const deleteProduct = async (id) => {
  const response = await apiClient.delete(`/products/${id}`);
  return response.data;
};

export const updateProductOrder = async (productIds) => {
  const response = await apiClient.put("/products/order", { productIds });
  return response.data;
};
