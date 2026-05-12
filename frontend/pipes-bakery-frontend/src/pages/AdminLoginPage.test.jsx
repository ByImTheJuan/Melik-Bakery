import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { MemoryRouter } from "react-router-dom";
import AdminLoginPage from "./AdminLoginPage";
import * as authService from "../services/authService";

const navigateMock = vi.fn();

vi.mock("react-router-dom", async () => {
  const actual = await vi.importActual("react-router-dom");
  return {
    ...actual,
    useNavigate: () => navigateMock,
  };
});

vi.mock("../services/authService", () => ({
  loginAdmin: vi.fn(),
}));

describe("AdminLoginPage", () => {
  beforeEach(() => {
    navigateMock.mockReset();
    authService.loginAdmin.mockReset();
  });

  it("submits admin credentials and redirects on success", async () => {
    authService.loginAdmin.mockResolvedValue({ authenticated: true });

    render(
      <MemoryRouter>
        <AdminLoginPage />
      </MemoryRouter>
    );

    fireEvent.change(screen.getByPlaceholderText("Introduce tu email"), {
      target: { value: "admin@melik.com" },
    });
    fireEvent.change(screen.getByPlaceholderText(/contrase/i), {
      target: { value: "P1p3P4n4d3r0" },
    });
    fireEvent.click(screen.getByRole("button", { name: /iniciar/i }));

    await waitFor(() => {
      expect(authService.loginAdmin).toHaveBeenCalledWith({
        email: "admin@melik.com",
        password: "P1p3P4n4d3r0",
      });
    });
    expect(navigateMock).toHaveBeenCalledWith("/admin");
  });

  it("shows the backend error when login fails", async () => {
    authService.loginAdmin.mockRejectedValue({
      response: {
        data: {
          message: "Credenciales inválidas.",
        },
      },
    });

    render(
      <MemoryRouter>
        <AdminLoginPage />
      </MemoryRouter>
    );

    fireEvent.change(screen.getByPlaceholderText("Introduce tu email"), {
      target: { value: "admin@melik.com" },
    });
    fireEvent.change(screen.getByPlaceholderText(/contrase/i), {
      target: { value: "wrong" },
    });
    fireEvent.click(screen.getByRole("button", { name: /iniciar/i }));

    expect(await screen.findByText("Credenciales inválidas.")).toBeInTheDocument();
  });
});
