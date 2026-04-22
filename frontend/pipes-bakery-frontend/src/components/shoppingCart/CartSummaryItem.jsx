import { formatCOP } from "../../utils/formatPrice.js";

const CartSummaryItem = ({ item }) => {
  return (
    <div className="summary-item">
      <div className="item-info">
        <p className="item-name">{item.productName}</p>
        <p className="item-quantity">x{item.quantity}</p>
      </div>

      <p className="item-price">
        ${formatCOP(item.quantity * item.unitPriceAtAdd)}
      </p>
    </div>
  );
};

export default CartSummaryItem;