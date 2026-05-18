import axios from "axios";

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  withCredentials: true,

  xsrfCookieName: "XSRF-TOKEN",
  xsrfHeaderName: "X-XSRF-TOKEN",

  headers: {
    "Content-Type": "application/json",
  },
});

apiClient.interceptors.response.use(
  response => response,

  error => {

    const status = error.response?.status;

    if (error.config?.suppressExpectedErrorLog) {
      return Promise.reject(error);
    }

    // Errores esperados
    if (status === 401 || status === 403) {
      return Promise.reject(error);
    }

    // Sólo en desarrollo
    if (import.meta.env.DEV) {
      console.error("API Error:", error);
    }

    return Promise.reject(error);
  }
);

export default apiClient;
