import { useNavigate } from "react-router-dom";
import "../../styles/global.css";
import { useAddToCart } from "../../hooks/useAddToCart";
import { formatCOP } from "../../utils/formatPrice";
import { getProductImageUrl } from "../../utils/productImage";


function ProductCard({ product, priority = false }) {
  const { addToCart } = useAddToCart();
  const navigate = useNavigate();

  const handleDetailsClick = () => {

    sessionStorage.setItem(
      "productsScrollPosition",
      window.scrollY
    );
    navigate(`/products/${product.id}`);
  };

  return (
    <div onClick={handleDetailsClick} className="product-card">
      <div className="product-image">
        <img
          src={getProductImageUrl(product.imageFile)}
          alt={product.name}
          loading={priority ? "eager" : "lazy"}
          decoding="async"
        />
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
    </div>
  );
}

export default ProductCard;
