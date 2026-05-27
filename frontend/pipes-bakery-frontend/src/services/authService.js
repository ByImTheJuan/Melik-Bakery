import apiClient from "../api/apiClient";

export async function loginAdmin(credentials) {
  await fetchAdminCsrfToken();
  const response = await apiClient.post("/auth/login", credentials);
  await fetchAdminCsrfToken();

  return response.data;
}

export async function checkAdminSession() {
  try {
    const sessionPath = import.meta.env.PROD ? "/auth/session-status" : "/auth/session";
    const response = await apiClient.get(sessionPath);

    if (response.data?.authenticated === false) {
      return false;
    }

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
