import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useDocumentTitle } from "../hooks/useDocumentTitle";
import { loginAdmin } from "../services/authService";
import "../styles/global.css";
import "../styles/adminLoginPage.css";

const initialForm = {
  email: "",
  password: "",
};

export default function AdminLoginPage() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState(initialForm);
  const [errorMessage, setErrorMessage] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  useDocumentTitle("Acceso admin");

  const handleChange = ({ target }) => {
    const { name, value } = target;
    setFormData((current) => ({
      ...current,
      [name]: value,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setErrorMessage("");
    setIsSubmitting(true);

    try {
      await loginAdmin(formData);
      navigate("/admin");
    } catch (error) {
      setErrorMessage(
        error.response?.data?.message ??
          "No se pudo iniciar sesión. Verifica tus credenciales."
      );
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="admin-login-page">
      <section className="admin-login-card">
        <span className="admin-login-eyebrow">Zona de administración</span>
        <h1>Iniciar sesión</h1>
        <p className="admin-login-copy">
          Accede con una cuenta administradora para gestionar el catálogo y los pedidos.
        </p>
        <p className="admin-login-disclaimer">
          Acceso restringido exclusivamente al personal administrativo de la panadería. Cualquier uso no autorizado está prohibido.
        </p>

        <form className="admin-login-form" onSubmit={handleSubmit}>
          <label className="admin-login-field">
            <span>Usuario</span>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              autoComplete="username"
              placeholder="Introduce tu email"
              required
            />
          </label>

          <label className="admin-login-field">
            <span>Contraseña</span>
            <input
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              autoComplete="current-password"
              placeholder="Introduce tu contraseña"
              required
            />
          </label>

          {errorMessage && <p className="admin-login-error">{errorMessage}</p>}

          <button
            type="submit"
            className="admin-login-button"
            disabled={isSubmitting}
          >
            {isSubmitting ? "Accediendo..." : "Iniciar sesión"}
          </button>
        </form>
      </section>
    </div>
  );
}
