import { formatCOP } from "../../utils/formatPrice.js";
import { describeCustomCake, isCustomCake } from "../../utils/customCake.js";

const CartSummaryItem = ({ item }) => {
  return (
    <div className="summary-item">
      <div className="item-info">
        <div>
          <p className="item-name">{item.productName}</p>
          {isCustomCake(item) && (
            <p className="item-detail">{describeCustomCake(item.customCake)}</p>
          )}
        </div>
        <p className="item-quantity">x{item.quantity}</p>
      </div>

      <p className="item-price">
        ${formatCOP(item.quantity * item.unitPriceAtAdd)}
      </p>
    </div>
  );
};

export default CartSummaryItem;
