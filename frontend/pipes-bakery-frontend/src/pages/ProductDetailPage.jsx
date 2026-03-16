import { useParams } from "react-router-dom";
import { useProduct } from "../hooks/useProduct";
import { formatCOP } from "../utils/formatPrice";
import { useNavigate } from "react-router-dom";
import { useState } from "react";
import "../styles/global.css";
import "../styles/productDetailPage.css";

function ProductDetailPage() {
  const { id } = useParams();
  const { product, status, errorMessage } = useProduct(id);
  const navigate = useNavigate();
  const [quantity, setQuantity] = useState(1);

  const increaseQty = () => {
    setQuantity((prev) => prev + 1);
  };

  const decreaseQty = () => {
    setQuantity((prev) => {
      if (prev <= 1) return 1;
      return prev - 1;
    });
  };

  const handleChange = (e) => {
    let value = e.target.value;

    if (value === "") {
      setQuantity("");
      return;
    }

    // solo números
    if (!/^[0-9]+$/.test(value)) {
      return;
    }

    const num = Number(value);

    if (isNaN(num)) return;

    if (num < 1) {
      setQuantity(1);
    } else {
      setQuantity(num);
    }
  };

  const handleKeyDown = (e) => {
    const invalidKeys = [
      "e",
      "E",
      "+",
      "-",
      ".",
      ","
    ];

    if (invalidKeys.includes(e.key)) {
      e.preventDefault();
    }
  };

  if (status === "loading") return <p>Cargando producto...</p>;
  if (status === "error") return <p>{errorMessage}</p>;
  if (status === "success" && !product)
    return <p>Producto no disponible.</p>;
  if (!product) return <p>Cargando producto...</p>;


  return (
    <div className="product-details-page">
      <div className="product-back" onClick={() => navigate("/products")}>← Volver al catálogo</div>
      <div className="product-details-container">
        <div className="product-details-img">
          <img src={`${import.meta.env.VITE_IMAGES_BASE_URL}${product.imageUrl}`} alt={product.name} />
        </div>
        <div className="product-details-info">
          <div className="product-details-header">
            <h2>{product.name}</h2>
            <span className="product-details-price">${formatCOP(product.price)}</span>
          </div>
          <div className="product-details-divider"></div>
          <p>{product.description}</p>
          <p>
            <strong>Ingredientes: </strong>
            {product.ingredients.join(", ")}
            .
          </p>
          <div className="product-actions">
            <div className="quantity-selector">
              <button className="qty-btn" onClick={decreaseQty}>−</button>
              <input className="qty-value" type="number" inputMode="numeric" min="1" value={quantity} onChange={handleChange} onKeyDown={handleKeyDown}/>
              <button className="qty-btn" onClick={increaseQty}>+</button>
            </div>
            <button className="product-details-button">Añadir al carrito</button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default ProductDetailPage;