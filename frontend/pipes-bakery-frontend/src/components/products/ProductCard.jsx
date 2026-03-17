import { Link } from "react-router-dom";
import "../../styles/global.css";
import { useAddToCart } from "../../hooks/useAddToCart";
import { formatCOP } from "../../utils/formatPrice";


function ProductCard({ product }) {
  const { addToCart } = useAddToCart();

  return (
    <Link to={`/products/${product.id}`} className="product-card">
      <div className="product-image">
        <img src={`${import.meta.env.VITE_IMAGES_BASE_URL}${product.imageUrl}`} alt={product.name} />
      </div>
      <div className="product-info">
        <div className="product-header">
          <h3>{product.name}</h3>
          <span className="product-price">${formatCOP(product.price)}</span>
        </div>
        <p className="product-description">{product.description}</p>
        <button className="product-button" onClick={(e) => {
          e.stopPropagation();
          e.preventDefault();
          addToCart(product.id, 1);
        }}>Añadir al carrito</button>
      </div>
    </Link>
  );
}

export default ProductCard;