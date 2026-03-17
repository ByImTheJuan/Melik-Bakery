import { Link } from "react-router-dom";
import { useLocation, useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import { useCart } from "../../context/CartContext";
import { FaShoppingCart } from "react-icons/fa";
import useActiveSection  from "../../hooks/useActiveSection";
import useScrollState from "../../hooks/useScrollState";

export default function Navbar() {
  const [menuOpen, setMenuOpen] = useState(false);
  const location = useLocation();
  const navigate = useNavigate();
  const isHome = location.pathname === "/";
  const activeSection = useActiveSection(isHome ? ["hero", "about", "contact"] : []);
  const scrolled = useScrollState(25);
  const { cart } = useCart();
  const [animate, setAnimate] = useState(false);
  const totalItems = cart?.items?.reduce((sum, item) => sum + item.quantity, 0) || 0;

  useEffect(() => {
    setAnimate(true);

    const t = setTimeout(() => {
      setAnimate(false);
    }, 200);

    return () => clearTimeout(t);

  }, [totalItems]);

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

  const closeMenu = () => {
    setMenuOpen(false);
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
              onClick={() => {
                handleHomeClick();
                closeMenu();
              }}
            >
              Inicio
            </button>

            <Link 
              to="/products"
              onClick={closeMenu}
              className={location.pathname.startsWith("/products") ? "active" : ""}
            >
              Productos
            </Link>
            
            <button 
              className={isHome && activeSection === "about" ? "active" : ""}
              onClick={() => {
                goToSection("about");
                closeMenu();
              }}>
                Quiénes somos
            </button>

            <button 
              className={isHome && activeSection === "contact" ? "active" : ""}
              onClick={() => {
                goToFooter();
                closeMenu();
              }}>
                Contacto
            </button>

          </div>

          <Link to={`/cart/${cart?.cartId}`} 
            className={`cart-button ${
              location.pathname.startsWith("/cart") ? "active" : ""
            }`}
            onClick={closeMenu}
          >
            <FaShoppingCart />
            
            {totalItems > 0 && (
              <span key={totalItems} className="cart-badge cart-badge-pop">
                {totalItems}
              </span>
            )}
          </Link>
          
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