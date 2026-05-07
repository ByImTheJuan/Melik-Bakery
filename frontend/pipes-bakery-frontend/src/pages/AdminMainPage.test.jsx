import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { MemoryRouter, Routes, Route } from "react-router-dom";
import AdminMainPage from "./AdminMainPage";
import * as authService from "../services/authService";

const navigateMock = vi.fn();

vi.mock("react-router-dom", async () => {
  const actual = await vi.importActual("react-router-dom");
  return {
    ...actual,
    useNavigate: () => navigateMock,
  };
});

vi.mock("../components/admin/ProductsAdmin", () => ({
  default: () => <div>Products admin content</div>,
}));

vi.mock("../components/admin/OrdersAdmin", () => ({
  default: () => <div>Orders admin content</div>,
}));

vi.mock("../services/authService", () => ({
  checkAdminSession: vi.fn(),
  logoutAdmin: vi.fn(),
}));

describe("AdminMainPage", () => {
  beforeEach(() => {
    navigateMock.mockReset();
    authService.checkAdminSession.mockResolvedValue(true);
    authService.logoutAdmin.mockResolvedValue();
  });

  it("redirects to login when there is no valid admin session", async () => {
    authService.checkAdminSession.mockResolvedValue(false);

    render(
      <MemoryRouter initialEntries={["/admin"]}>
        <Routes>
          <Route path="/admin" element={<AdminMainPage />} />
          <Route path="/admin/login" element={<div>Login route</div>} />
        </Routes>
      </MemoryRouter>
    );

    expect(await screen.findByText("Login route")).toBeInTheDocument();
  });

  it("switches sections and logs out correctly", async () => {
    render(
      <MemoryRouter initialEntries={["/admin"]}>
        <Routes>
          <Route path="/admin" element={<AdminMainPage />} />
        </Routes>
      </MemoryRouter>
    );

    expect(await screen.findByText("Products admin content")).toBeInTheDocument();

    fireEvent.click(screen.getByRole("button", { name: "Gestión de Pedidos" }));
    expect(screen.getByText("Orders admin content")).toBeInTheDocument();

    fireEvent.click(screen.getByRole("button", { name: "Cerrar sesión" }));

    await waitFor(() => {
      expect(authService.logoutAdmin).toHaveBeenCalledTimes(1);
      expect(navigateMock).toHaveBeenCalledWith("/admin/login", { replace: true });
    });
  });
});
