import { Link } from "react-router-dom";
import { useLocation, useNavigate } from "react-router-dom";
import { useState } from "react";
import useActiveSection  from "../../hooks/useActiveSection";
import useScrollState from "../../hooks/useScrollState";

export default function Navbar() {
  const [menuOpen, setMenuOpen] = useState(false);
  const location = useLocation();
  const navigate = useNavigate();
  const isHome = location.pathname === "/";
  const activeSection = useActiveSection(isHome ? ["hero", "about", "contact"] : []);
  const scrolled = useScrollState(25);


  const goToSection = (sectionId) => {
    navigate("/", { state: { scrollTo: sectionId } });
    setMenuOpen(false);
  };

  const goToFooter = () => {
    const footer = document.getElementById("contact");

    if (footer) {
      footer.scrollIntoView({ behavior: "smooth" });
    }
  };
    
  const handleHomeClick = () => {
      navigate("/");
      window.scrollTo({top: 0, behavior: "smooth"});
  };

  return (
    <nav className={`navbar ${scrolled ? "navbar-scrolled" : ""}`}>
        <div className="nav-logo" alt="Pipes Bakery" onClick={handleHomeClick}>
            <img src="/images/logo_2.png" alt="Pipe's Bakery" />
        </div>

        <div className="nav-right">
          <div className={`nav-links ${menuOpen ? "open" : ""}`}>
            <button 
              className={isHome && activeSection === "hero" ? "active" : ""}
              onClick={handleHomeClick}
            >
              Inicio
            </button>

            <Link 
              to="/products"
              className={location.pathname.startsWith("/products") ? "active" : ""}
            >
              Productos
            </Link>
            
            <button 
              className={isHome && activeSection === "about" ? "active" : ""}
              onClick={() => goToSection("about")}>
                Quiénes somos
            </button>

              <button 
                className={isHome && activeSection === "contact" ? "active" : ""}
                onClick={() => goToFooter()}>
                  Contacto
              </button>
          </div>
          
          <button
            className={`menu-toggle ${menuOpen ? "open" : ""}`}
            onClick={() => setMenuOpen(!menuOpen)}
          >
            <span></span>
            <span></span>
            <span></span>
          </button>
        </div>

    </nav>
  );
}