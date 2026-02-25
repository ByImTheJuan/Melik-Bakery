import { Link } from "react-router-dom";
import { useLocation, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";

export default function Navbar() {
  const [scrolled, setScrolled] = useState(false);
  const [menuOpen, setMenuOpen] = useState(false);
  const location = useLocation();
  const navigate = useNavigate();
  const [activeSection, setActiveSection] = useState("hero");

  const goToSection = (sectionId) => {
    navigate("/", { state: { scrollTo: sectionId } });
    setMenuOpen(false);
  };

  useEffect(() => {
    const sections = ["hero", "about", "contact"];

    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            setActiveSection(entry.target.id);
          }
        });
      },
      {
        threshold: 0.6, // importante
        rootMargin: "-80px 0px 0px 0px" // para compensar la altura del navbar
      }
    );

    sections.forEach((id) => {
      const el = document.getElementById(id);
      if (el) observer.observe(el);
    });

    return () => observer.disconnect();
  }, []);
    
const handleHomeClick = () => {
    navigate("/");
    window.scrollTo({top: 0, behavior: "smooth"});
};

  useEffect(() => {
    const handleScroll = () => {
      if (window.scrollY > 25) {
        setScrolled(true);
      } else {
        setScrolled(false);
      }
    };

    window.addEventListener("scroll", handleScroll);

    return () => window.removeEventListener("scroll", handleScroll);
  }, []);

  return (
    <nav className={`navbar ${scrolled ? "navbar-scrolled" : ""}`}>
        <div className="nav-logo" alt="Pipes Bakery" onClick={handleHomeClick}>
            <img src="/images/logo_2.png" alt="Pipe's Bakery" />
        </div>

        <div className="nav-right">
          <div className={`nav-links ${menuOpen ? "open" : ""}`}>
            <button 
              className={activeSection === "hero" ? "active" : ""}
              onClick={handleHomeClick}
            >
              Inicio
            </button>

            <Link to="/products">Productos</Link>
            
            <button 
              className={activeSection === "about" ? "active" : ""}
              onClick={() => goToSection("about")}>
                Quiénes somos
            </button>

              <button 
                className={activeSection === "contact" ? "active" : ""}
                onClick={() => goToSection("contact")}>
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