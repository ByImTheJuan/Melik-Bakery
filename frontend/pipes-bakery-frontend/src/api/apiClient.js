import axios from "axios";

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  withCredentials: true,
  headers: {
    "Content-Type": "application/json",
  },
});

const unsafeMethods = new Set(["post", "put", "patch", "delete"]);
const adminCsrfPaths = [
  "/auth/logout",
  "/products",
  "/orders",
  "/clients",
  "/addresses",
];

function getCookieValue(name) {
  const cookies = document.cookie ? document.cookie.split("; ") : [];
  const cookie = cookies.find((item) => item.startsWith(`${name}=`));

  if (!cookie) {
    return null;
  }

  return decodeURIComponent(cookie.substring(name.length + 1));
}

function shouldAttachCsrfToken(config) {
  const method = config.method?.toLowerCase();
  const url = config.url ?? "";

  return unsafeMethods.has(method) && adminCsrfPaths.some((path) => url.startsWith(path));
}

apiClient.interceptors.request.use((config) => {
  if (shouldAttachCsrfToken(config)) {
    const csrfToken = getCookieValue("XSRF-TOKEN");

    if (csrfToken) {
      config.headers["X-XSRF-TOKEN"] = csrfToken;
    }
  }

  return config;
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
