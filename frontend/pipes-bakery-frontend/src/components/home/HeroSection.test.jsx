import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it, vi } from "vitest";
import { MemoryRouter } from "react-router-dom";
import HeroSection from "./HeroSection";

const navigateMock = vi.fn();

vi.mock("react-router-dom", async () => {
  const actual = await vi.importActual("react-router-dom");
  return { ...actual, useNavigate: () => navigateMock };
});

describe("HeroSection", () => {
  it("leads with the cake designer and keeps the catalog as the second action", async () => {
    const user = userEvent.setup();
    render(
      <MemoryRouter>
        <HeroSection />
      </MemoryRouter>
    );

    const buttons = screen.getAllByRole("button");
    expect(buttons.map((button) => button.textContent)).toEqual(["Diseña tu torta", "Ver catálogo"]);

    await user.click(buttons[0]);
    expect(navigateMock).toHaveBeenCalledWith("/personalizar");

    await user.click(buttons[1]);
    expect(navigateMock).toHaveBeenCalledWith("/products");
  });
});
