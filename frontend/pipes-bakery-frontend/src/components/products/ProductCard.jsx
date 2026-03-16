import { Link } from "react-router-dom";
import "../../styles/global.css";
import { formatCOP } from "../../utils/formatPrice";


function ProductCard({ product }) {
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
        <button className="product-button">Añadir al carrito</button>
      </div>
    </Link>
  );
}

export default ProductCard;