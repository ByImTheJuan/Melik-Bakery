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

  it("attaches csrf token to unsafe admin requests", () => {
    document.cookie = "XSRF-TOKEN=csrf-token";

    const config = apiClient.interceptors.request.handlers[0].fulfilled({
      method: "put",
      url: "/products/1",
      headers: {},
    });

    expect(config.headers["X-XSRF-TOKEN"]).toBe("csrf-token");
  });

  it("does not attach csrf token to public cart requests", () => {
    document.cookie = "XSRF-TOKEN=csrf-token";

    const config = apiClient.interceptors.request.handlers[0].fulfilled({
      method: "post",
      url: "/cart",
      headers: {},
    });

    expect(config.headers["X-XSRF-TOKEN"]).toBeUndefined();
  });
});
