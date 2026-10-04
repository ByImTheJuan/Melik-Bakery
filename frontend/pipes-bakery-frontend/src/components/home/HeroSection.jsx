import { useNavigate } from "react-router-dom";


export default function HeroSection() {
 const navigate = useNavigate();

  return (
    <section id="hero" className="hero">
        <div className="hero-content">
            <img src="/images/logo.webp" alt="Melik Bakery" className="hero-logo" />
            <div className="hero-copy">
              <p className="hero-tagline">Crea tu torta perfecta para cumpleaños, bodas, comuniones y cualquier ocasión especial.</p>
              <div className="hero-actions">
                <button className="hero-primary" onClick={() => navigate("/personalizar")}>
                  Diseña tu torta
                </button>
                <button className="hero-secondary" onClick={() => navigate("/products")}>
                  Ver catálogo
                </button>
              </div>
            </div>
        </div>
    </section>
  );
}
