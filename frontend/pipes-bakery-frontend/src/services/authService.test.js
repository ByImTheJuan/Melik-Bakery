import { beforeEach, describe, expect, it, vi } from "vitest";
import apiClient from "../api/apiClient";
import { checkAdminSession, fetchAdminCsrfToken, loginAdmin, logoutAdmin } from "./authService";

vi.mock("../api/apiClient", () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
  },
}));

describe("authService", () => {
  beforeEach(() => {
    apiClient.get.mockReset();
    apiClient.post.mockReset();
  });

  it("logs in an admin without storing a token in localStorage", async () => {
    apiClient.post.mockResolvedValue({
      data: { authenticated: true },
    });

    const response = await loginAdmin({
      email: "admin@melik.com",
      password: "secret",
    });

    expect(apiClient.post).toHaveBeenCalledWith("/auth/login", {
      email: "admin@melik.com",
      password: "secret",
    });
    expect(localStorage.getItem("ADMIN_AUTH_TOKEN")).toBeNull();
    expect(response).toEqual({ authenticated: true });
  });

  it("checks whether the admin session cookie is valid", async () => {
    apiClient.get.mockResolvedValue({});

    await expect(checkAdminSession()).resolves.toBe(true);

    expect(apiClient.get).toHaveBeenCalledWith("/auth/session");
    expect(apiClient.get).toHaveBeenCalledWith("/auth/csrf");
  });

  it("returns false when the admin session cookie is invalid", async () => {
    apiClient.get.mockRejectedValue(new Error("Unauthorized"));

    await expect(checkAdminSession()).resolves.toBe(false);
  });

  it("logs out by asking the backend to clear the cookie", async () => {
    apiClient.get.mockResolvedValue({});
    apiClient.post.mockResolvedValue({});

    await logoutAdmin();

    expect(apiClient.get).toHaveBeenCalledWith("/auth/csrf");
    expect(apiClient.post).toHaveBeenCalledWith("/auth/logout");
  });

  it("fetches the admin csrf token", async () => {
    apiClient.get.mockResolvedValue({
      data: { headerName: "X-XSRF-TOKEN", token: "csrf-token" },
    });

    await expect(fetchAdminCsrfToken()).resolves.toEqual({
      headerName: "X-XSRF-TOKEN",
      token: "csrf-token",
    });

    expect(apiClient.get).toHaveBeenCalledWith("/auth/csrf");
  });
});
