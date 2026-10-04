import { render, screen, within } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import { MemoryRouter } from "react-router-dom";
import Footer from "./Footer";

vi.mock("../../hooks/useInView", () => ({
  default: () => [{ current: null }, true],
}));

describe("Footer", () => {
  it("links to the privacy and cookie policies", () => {
    render(
      <MemoryRouter>
        <Footer />
      </MemoryRouter>
    );

    const legal = screen.getByRole("navigation", { name: "Información legal" });
    expect(legal).toBeInTheDocument();
    expect(screen.getByRole("link", { name: "Política de Privacidad" })).toHaveAttribute(
      "href",
      "/politica-de-privacidad"
    );
    expect(screen.getByRole("link", { name: "Política de Cookies" })).toHaveAttribute(
      "href",
      "/politica-de-cookies"
    );
  });

  it("groups contact and social links under their own headings", () => {
    render(
      <MemoryRouter>
        <Footer />
      </MemoryRouter>
    );

    expect(screen.getByRole("img", { name: "Melik Bakery" })).toHaveAttribute("src", "/images/logo.webp");

    const contact = screen.getByRole("region", { name: "Contáctanos" });
    expect(within(contact).getByRole("link", { name: /\+57 319 383 0446/ })).toHaveAttribute(
      "href",
      "https://wa.me/573193830446"
    );
    expect(within(contact).getByRole("link", { name: /melik\.bakery@hyd\.net\.co/ })).toHaveAttribute(
      "href",
      "mailto:melik.bakery@hyd.net.co"
    );

    const social = screen.getByRole("region", { name: "Síguenos en redes sociales" });
    expect(within(social).getByRole("link", { name: /Instagram/ })).toHaveAttribute(
      "href",
      "https://instagram.com/melik.bakery"
    );
    expect(within(social).getByRole("link", { name: /TikTok/ })).toHaveAttribute(
      "href",
      "https://tiktok.com/@melik.bakery"
    );
  });
});
