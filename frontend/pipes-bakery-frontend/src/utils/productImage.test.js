import { afterEach, describe, expect, it, vi } from "vitest";
import { getProductImageUrl } from "./productImage";

describe("getProductImageUrl", () => {
  afterEach(() => {
    vi.unstubAllEnvs();
  });

  it("joins a base URL with a trailing slash and a plain file name", () => {
    vi.stubEnv("VITE_IMAGES_BASE_URL", "/api/images/");
    expect(getProductImageUrl("cinnamonRoll.jpg")).toBe("/api/images/cinnamonRoll.jpg");
  });

  it("adds the missing slash when the base URL has none", () => {
    vi.stubEnv("VITE_IMAGES_BASE_URL", "http://localhost:8080/images");
    expect(getProductImageUrl("cinnamonRoll.jpg")).toBe("http://localhost:8080/images/cinnamonRoll.jpg");
  });

  it("strips a legacy leading slash from the file name", () => {
    vi.stubEnv("VITE_IMAGES_BASE_URL", "/api/images/");
    expect(getProductImageUrl("/cinnamonRoll.jpg")).toBe("/api/images/cinnamonRoll.jpg");
  });

  it("returns an empty string when there is no file name", () => {
    vi.stubEnv("VITE_IMAGES_BASE_URL", "/api/images/");
    expect(getProductImageUrl("")).toBe("");
    expect(getProductImageUrl(null)).toBe("");
  });
});
