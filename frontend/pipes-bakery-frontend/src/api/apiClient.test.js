import { beforeEach, describe, expect, it, vi } from "vitest";
import apiClient from "./apiClient";

describe("apiClient interceptors", () => {
  beforeEach(() => {
    console.error = vi.fn();
    document.cookie = "XSRF-TOKEN=; Max-Age=0";
  });

  it("sends cookies with API requests", () => {
    expect(apiClient.defaults.withCredentials).toBe(true);
  });

  it("adds the CSRF header to login and administrative mutations", async () => {
    document.cookie = "XSRF-TOKEN=csrf-token";

    const loginConfig = await apiClient.interceptors.request.handlers[0].fulfilled({
      method: "post",
      url: "/auth/login",
      headers: {},
    });
    const updateConfig = await apiClient.interceptors.request.handlers[0].fulfilled({
      method: "put",
      url: "/products/1",
      headers: {},
    });

    expect(loginConfig.headers["X-XSRF-TOKEN"]).toBe("csrf-token");
    expect(updateConfig.headers["X-XSRF-TOKEN"]).toBe("csrf-token");
  });

  it("rejects response errors", async () => {
    const error = new Error("boom");

    await expect(
      apiClient.interceptors.response.handlers[0].rejected(error)
    ).rejects.toBe(error);
  });

  it("does not log errors marked as expected", async () => {
    const error = {
      config: { suppressExpectedErrorLog: true },
      response: { status: 404 },
    };

    await expect(
      apiClient.interceptors.response.handlers[0].rejected(error)
    ).rejects.toBe(error);

    expect(console.error).not.toHaveBeenCalled();
  });
});
