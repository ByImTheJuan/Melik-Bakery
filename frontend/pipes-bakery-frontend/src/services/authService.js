import apiClient from "../api/apiClient";

export async function loginAdmin(credentials) {
  const response = await apiClient.post("/auth/login", credentials);

  return response.data;
}

export async function checkAdminSession() {
  try {
    await apiClient.get("/auth/session");
    return true;
  } catch {
    return false;
  }
}

export async function logoutAdmin() {
  await apiClient.post("/auth/logout");
}
