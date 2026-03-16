export const handleAxiosError = (error) => {
  if (error.response) {
    const statusCode = error.response.status;

    if (statusCode === 404) {
      return "Recurso no encontrado.";
    }

    if (statusCode === 400) {
      return "Solicitud inválida.";
    }

    if (statusCode === 401) {
      return "No autorizado.";
    }

    if (statusCode >= 500) {
      return "Error interno del servidor.";
    }

    return "Error al procesar la solicitud.";
  }

  if (error.request) {
    return "No se pudo conectar con el servidor.";
  }

  return "Error inesperado.";
};