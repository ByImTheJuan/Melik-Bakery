import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { MemoryRouter } from "react-router-dom";
import PrivacyPolicyPage from "./PrivacyPolicyPage";

describe("PrivacyPolicyPage", () => {
  it("identifies the data controller and links to the cookie policy", () => {
    render(
      <MemoryRouter>
        <PrivacyPolicyPage />
      </MemoryRouter>
    );

    expect(screen.getByRole("heading", { level: 1, name: "Política de Privacidad" })).toBeInTheDocument();
    expect(screen.getByText(/HYD S\.A\.S\. \(marca comercial Melik Bakery\)/)).toBeInTheDocument();
    expect(screen.getByText("830021364-7", { exact: false })).toBeInTheDocument();
    expect(screen.getByRole("link", { name: "Política de Cookies" })).toHaveAttribute(
      "href",
      "/politica-de-cookies"
    );
  });
});
