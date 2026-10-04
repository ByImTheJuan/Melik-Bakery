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

  it("keeps the folder of custom cake photos while encoding each segment", () => {
    vi.stubEnv("VITE_IMAGES_BASE_URL", "/api/images/");
    expect(getProductImageUrl("custom-cakes/cake-ab12.jpg")).toBe("/api/images/custom-cakes/cake-ab12.jpg");
    expect(getProductImageUrl("my cake.jpg")).toBe("/api/images/my%20cake.jpg");
  });

  it("returns an empty string when there is no file name", () => {
    vi.stubEnv("VITE_IMAGES_BASE_URL", "/api/images/");
    expect(getProductImageUrl("")).toBe("");
    expect(getProductImageUrl(null)).toBe("");
  });
});
