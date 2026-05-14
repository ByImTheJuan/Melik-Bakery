import { beforeEach, describe, expect, it, vi } from "vitest";
import apiClient from "./apiClient";

describe("apiClient interceptors", () => {
  beforeEach(() => {
    console.error = vi.fn();
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
});
