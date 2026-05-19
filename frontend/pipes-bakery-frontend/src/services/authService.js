import apiClient from "../api/apiClient";

export async function loginAdmin(credentials) {
  const response = await apiClient.post("/auth/login", credentials);

  await fetchAdminCsrfToken();

  return response.data;
}

export async function checkAdminSession() {
  try {
    await apiClient.get("/auth/session");
    await fetchAdminCsrfToken();
    return true;
  } catch {
    return false;
  }
}

export async function fetchAdminCsrfToken() {
  const response = await apiClient.get("/auth/csrf");

  return response.data;
}

export async function logoutAdmin() {
  await fetchAdminCsrfToken();
  await apiClient.post("/auth/logout");
}
