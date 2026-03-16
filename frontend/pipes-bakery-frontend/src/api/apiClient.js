import axios from "axios";

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

apiClient.interceptors.response.use(
  response => response,
  error => {
    console.error("API Error:", error);
    return Promise.reject(error);
  }
);

export default apiClient;