import { useMemo, useState } from "react";
import { Navigate, useNavigate } from "react-router-dom";
import ProductsAdmin from "../components/admin/ProductsAdmin";
import { useDocumentTitle } from "../hooks/useDocumentTitle";
import { getAdminToken, removeAdminToken } from "../services/authStorage";
import "../styles/global.css";
import "../styles/adminMainPage.css";

const sections = [
  { id: "products", label: "Gestión de Productos" },
  { id: "orders", label: "Gestión de Pedidos" },
];

function OrdersPlaceholder() {
  return (
    <section className="admin-placeholder">
      <span className="admin-placeholder-badge">Próximamente</span>
      <h2>Gestión de pedidos</h2>
      <p>
        Esta sección queda preparada en el menú para que en el siguiente paso conectemos el listado y detalle de pedidos.
      </p>
    </section>
  );
}

export default function AdminMainPage() {
  const navigate = useNavigate();
  const token = getAdminToken();
  const [activeSection, setActiveSection] = useState("products");

  useDocumentTitle("Panel de administración");

  const activeContent = useMemo(() => {
    if (activeSection === "orders") {
      return <OrdersPlaceholder />;
    }

    return <ProductsAdmin />;
  }, [activeSection]);

  if (!token) {
    return <Navigate to="/admin/login" replace />;
  }

  function handleLogout() {
    removeAdminToken();
    navigate("/admin/login", { replace: true });
  }

  return (
    <div className="admin-main-page">
      <aside className="admin-sidebar">
        <div className="admin-sidebar-top">
          <span className="admin-sidebar-eyebrow">Melik Bakery</span>
          <h1>Panel Admin</h1>
          <p>Gestiona el catálogo y prepara el área de pedidos desde una sola vista.</p>
        </div>

        <nav className="admin-sidebar-nav" aria-label="Secciones de administración">
          {sections.map((section) => (
            <button
              key={section.id}
              className={activeSection === section.id ? "admin-nav-button active" : "admin-nav-button"}
              onClick={() => setActiveSection(section.id)}
            >
              {section.label}
            </button>
          ))}
        </nav>

        <button className="admin-logout-button" onClick={handleLogout}>
          Cerrar sesión
        </button>
      </aside>

      <div className="admin-content">
        {activeContent}
      </div>
    </div>
  );
}
