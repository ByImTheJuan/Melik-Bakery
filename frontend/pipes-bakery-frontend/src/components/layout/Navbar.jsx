import { Link } from "react-router-dom";
import { useEffect, useState } from "react";

export default function Navbar() {
  const [scrolled, setScrolled] = useState(false);
  const [menuOpen, setMenuOpen] = useState(false);

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
        <div className="nav-logo" alt="Pipes Bakery">
            <img src="/images/logo_2.png" alt="Pipe's Bakery" />
        </div>

        <div className="nav-right">
          <div className={`nav-links ${menuOpen ? "open" : ""}`}>
            <Link to="/">Inicio</Link>
            <Link to="/products">Productos</Link>
            <Link to="/about">Sobre Nosotros</Link>
            <Link to="/contact">Contáctanos</Link>
          </div>
          
          <button
            className="menu-toggle"
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