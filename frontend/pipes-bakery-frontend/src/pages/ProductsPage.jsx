import { useEffect } from "react";
import ProductCard from "../components/products/ProductCard";
import { useProducts } from "../hooks/useAllProducts";
import { useDocumentTitle } from "../hooks/useDocumentTitle";
import "../styles/global.css";
import "../styles/productsPage.css";

function ProductsPage() {
  const { products, status, errorMessage } = useProducts();
  useDocumentTitle("Productos");
  
  useEffect(() => {

    const savedScroll = sessionStorage.getItem(
      "productsScrollPosition"
    );

    if (savedScroll) {

      setTimeout(() => {
        window.scrollTo({
          top: Number(savedScroll),
          behavior: "instant"
        });
      }, 500);

      sessionStorage.removeItem(
        "productsScrollPosition"
      );
    }
  }, []);

  if (status === "loading") return <p>Cargando productos...</p>;
  if (status === "error") return <p>{errorMessage}</p>;
  if (status === "success" && products.length === 0)
    return <p>No hay productos disponibles actualmente.</p>;

  return (
    <div className="products-container">
      <h1>Nuestros productos</h1>
      <div className="products-grid">
        {products.map((product) => (
          <ProductCard key={product.id} product={product} />
        ))}
      </div>
      <div className="products-note">
        * ¿No encuentras lo que buscas? Contáctanos para pedidos personalizados.
      </div>
    </div>
  );
}

export default ProductsPage;