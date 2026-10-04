import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { MemoryRouter } from "react-router-dom";
import CookiePolicyPage from "./CookiePolicyPage";

describe("CookiePolicyPage", () => {
  it("lists every cookie and storage key the site really uses", () => {
    render(
      <MemoryRouter>
        <CookiePolicyPage />
      </MemoryRouter>
    );

    expect(screen.getByRole("heading", { level: 1, name: "Política de Cookies" })).toBeInTheDocument();
    for (const name of ["XSRF-TOKEN", "ADMIN_AUTH_TOKEN", "cartId", "productsScrollPosition"]) {
      expect(screen.getByText(name)).toBeInTheDocument();
    }
    expect(screen.getByRole("link", { name: "Política de Privacidad" })).toHaveAttribute(
      "href",
      "/politica-de-privacidad"
    );
  });
});
