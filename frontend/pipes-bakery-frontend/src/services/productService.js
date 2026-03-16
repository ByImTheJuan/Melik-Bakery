import apiClient from "../api/apiClient";


const API_BASE_URL = "http://localhost:8080/api/products";

export const getAllProducts = async () => {
  const response = await apiClient.get("/products");
  return response.data;
};

export const getProductById = async (id) => {
  const response = await apiClient.get(`/products/${id}`);
  return await response.data;
};