import { render, screen } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import { MemoryRouter } from "react-router-dom";
import Layout from "./Layout";

vi.mock("./Navbar", () => ({
  default: () => <div>Navbar test</div>,
}));

vi.mock("./Footer", () => ({
  default: () => <div>Footer test</div>,
}));

describe("Layout", () => {
  it("shows the navbar on public routes", () => {
    render(
      <MemoryRouter initialEntries={["/products"]}>
        <Layout>
          <div>Contenido</div>
        </Layout>
      </MemoryRouter>
    );

    expect(screen.getByText("Navbar test")).toBeInTheDocument();
    expect(screen.getByText("Footer test")).toBeInTheDocument();
  });

  it("hides the navbar on admin routes", () => {
    render(
      <MemoryRouter initialEntries={["/admin"]}>
        <Layout>
          <div>Contenido admin</div>
        </Layout>
      </MemoryRouter>
    );

    expect(screen.queryByText("Navbar test")).not.toBeInTheDocument();
    expect(screen.getByText("Footer test")).toBeInTheDocument();
  });
});
