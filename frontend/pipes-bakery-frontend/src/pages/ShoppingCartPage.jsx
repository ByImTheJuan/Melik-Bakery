import { useCart } from "../hooks/useCart";
import { formatCOP } from "../utils/formatPrice";
import { useNavigate } from "react-router-dom";
import { useDocumentTitle } from "../hooks/useDocumentTitle";
import CartItemCard from "../components/shoppingCart/CartItemCard";

import "../styles/global.css";
import "../styles/shoppingCartPage.css";

function ShoppingCartPage() {
  useDocumentTitle("Carrito");
  const navigate = useNavigate();
  const { cart, loading } = useCart();
  const total =
    cart?.items?.reduce(
        (sum, item) =>
        sum + item.unitPriceAtAdd * item.quantity,
        0
    ) || 0;


  if (loading) {
    return <div>Cargando carrito...</div>;
  }


  if (!cart || cart.items.length === 0) {
    return (
      <div className="cart-empty-container">
        <h1>Tu Carrito</h1>
        <p>Todavía no tienes nada en tu carrito.</p>
        <button className="cart-empty-button" onClick={() => window.location.href = "/products"}>
          Ver productos
        </button>
      </div>
    );
  }


  return (
    <div className="cart-container">
      <h1>Tu Carrito</h1>
      <div className="cart-grid">

        <div className="cart-header">
          <div className="cart-header-delete"></div>
          <div className="cart-header-image"></div>
          <div className="cart-header-name">Producto</div>
          <div className="cart-header-price">Precio</div>
          <div className="cart-header-quantity">Cantidad</div>
          <div className="cart-header-total">Subtotal</div>
        </div>

        {cart.items.map((item) => (

          <CartItemCard
            key={item.productId}
            cartItem={item}
          />

        ))}

      </div>
      <div className="cart-summary">

        <div className="cart-summary-total">
        Total: ${formatCOP(total)}
        </div>

        <button className="cart-checkout-button"
            onClick={() => navigate(`/checkout/${cart?.cartId}`)}>
        Proceder al checkout
        </button>

      </div>

    </div>
  );
}

export default ShoppingCartPage;