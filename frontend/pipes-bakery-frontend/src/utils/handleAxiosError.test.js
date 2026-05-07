import { describe, expect, it } from "vitest";
import { handleAxiosError } from "./handleAxiosError";

describe("handleAxiosError", () => {
  it("maps known HTTP status codes to user-friendly messages", () => {
    expect(handleAxiosError({ response: { status: 404 } })).toBe("Recurso no encontrado.");
    expect(handleAxiosError({ response: { status: 400 } })).toBe("Solicitud inválida.");
    expect(handleAxiosError({ response: { status: 401 } })).toBe("No autorizado.");
    expect(handleAxiosError({ response: { status: 500 } })).toBe("Error interno del servidor.");
  });

  it("handles connection errors", () => {
    expect(handleAxiosError({ request: {} })).toBe("No se pudo conectar con el servidor.");
  });

  it("handles unexpected errors", () => {
    expect(handleAxiosError({})).toBe("Error inesperado.");
  });
});
