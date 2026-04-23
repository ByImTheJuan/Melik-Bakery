import CartSummaryItem from "../shoppingCart/CartSummaryItem";
import { formatCOP } from "../../utils/formatPrice.js";

const OrderSummary = ({ cart, onCheckout, loading, errors = [] }) => {
  const itemsTotal = cart.itemsTotal || 0;
  const shippingCost = cart.shippingCost || 0;
  const total = cart.totalPrice || 0;

  return (
    <div className="order-summary-box">
      <h2>Resumen del pedido</h2>

      <div className="summary-items">
        {cart.items.map((item) => (
          <CartSummaryItem key={item.productId} item={item} />
        ))}
      </div>

      <div className="summary-divider" />

      <div className="summary-subtotal">
        <span>Subtotal</span>
        <span>${formatCOP(itemsTotal)}</span>
      </div>

      <div className="summary-shipping">
        <span>Gastos de envío</span>
        <span>
          {shippingCost === 0
            ? "Gratis"
            : `${formatCOP(shippingCost)}`}
        </span>
      </div>

      <div className="summary-total">
        <span>Total</span>
        <span>${formatCOP(total)}</span>
      </div>

      {errors.length > 0 && (
        <div className="error">
          <strong>Revisa estos datos antes de continuar:</strong>
          <ul className="error-list">
            {errors.map((message) => (
              <li key={message}>{message}</li>
            ))}
          </ul>
        </div>
      )}

      <button className="checkout-button" onClick={onCheckout} disabled={loading}>
        {loading ? "Procesando..." : "Confirmar pedido"}
      </button>
    </div>
  );
};

export default OrderSummary;
