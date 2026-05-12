import { useEffect, useMemo, useState } from "react";
import { Navigate, useNavigate } from "react-router-dom";
import OrdersAdmin from "../components/admin/OrdersAdmin";
import ProductsAdmin from "../components/admin/ProductsAdmin";
import { useDocumentTitle } from "../hooks/useDocumentTitle";
import { checkAdminSession, logoutAdmin } from "../services/authService";
import "../styles/global.css";
import "../styles/adminMainPage.css";

const sections = [
  { id: "products", label: "Gestión de Productos" },
  { id: "orders", label: "Gestión de Pedidos" },
];

export default function AdminMainPage() {
  const navigate = useNavigate();
  const [authStatus, setAuthStatus] = useState("checking");
  const [activeSection, setActiveSection] = useState("products");

  useDocumentTitle("Panel de administración");

  const activeContent = useMemo(() => {
    if (activeSection === "orders") {
      return <OrdersAdmin />;
    }

    return <ProductsAdmin />;
  }, [activeSection]);

  useEffect(() => {
    let isMounted = true;

    checkAdminSession().then((isAuthenticated) => {
      if (isMounted) {
        setAuthStatus(isAuthenticated ? "authenticated" : "unauthenticated");
      }
    });

    return () => {
      isMounted = false;
    };
  }, []);

  if (authStatus === "checking") {
    return <div className="admin-main-page">Comprobando sesion...</div>;
  }

  if (authStatus === "unauthenticated") {
    return <Navigate to="/admin/login" replace />;
  }

  async function handleLogout() {
    try {
      await logoutAdmin();
    } finally {
      setAuthStatus("unauthenticated");
    }

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
