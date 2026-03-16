import ProductCard from "../components/products/ProductCard";
import { useProducts } from "../hooks/useAllProducts";
import "../styles/global.css";
import "../styles/productsPage.css";

function ProductsPage() {
  const { products, status, errorMessage } = useProducts();

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
    </div>
  );
}

export default ProductsPage;