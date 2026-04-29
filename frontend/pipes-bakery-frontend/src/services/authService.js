import apiClient from "../api/apiClient";
import { setAdminToken } from "./authStorage";

export async function loginAdmin(credentials) {
  const response = await apiClient.post("/auth/login", credentials);
  const { token } = response.data;

  setAdminToken(token);

  return token;
}
