import { Link } from "react-router-dom";

export default function Navbar() {
  return (
    <nav style={{ padding: "1rem", borderBottom: "1px solid #ccc" }}>
      <Link to="/" style={{ marginRight: "1rem" }}>
        Inicio
      </Link>
      <Link to="/products">Productos</Link>
    </nav>
  );
}