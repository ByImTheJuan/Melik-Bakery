import { useNavigate } from "react-router-dom";


export default function HeroSection() {
 const navigate = useNavigate();

  return (
    <section id="hero" className="hero">
        <div className="hero-content">
            <img src="/images/logo.webp" alt="Logo" />
            <p>Crea tu torta perfecta para cumpleaños, bodas, comuniones y cualquier ocasión especial.</p>
            <div className="hero-actions">
              <button className="hero-primary" onClick={() => navigate("/personalizar")}>
                Diseña tu torta
              </button>
              <button className="hero-secondary" onClick={() => navigate("/products")}>
                Ver catálogo
              </button>
            </div>
        </div>
    </section>
  );
}
