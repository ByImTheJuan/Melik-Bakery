import { Link } from "react-router-dom";

export default function Navbar() {
  return (
    <nav className="navbar">
        <div className="nav-logo">Pipes Bakery
            <img src="/images/logo.pnga" alt="Pipe's Bakery" /> {/* Asegúrate de que la ruta de la imagen sea correcta */}
        </div>

        <div className="nav-links">
            <Link to="/">Inicio</Link>
            <Link to="/products">Productos</Link>
            <Link to="/about">Sobre Nosotros</Link>
            <Link to="/contact">Contacto</Link>
        </div>

    </nav>
  );
}