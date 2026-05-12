import { useCart } from "../../hooks/useCart";
import { formatCOP } from "../../utils/formatPrice";
import "../../styles/global.css";


function CartItemCard({ cartItem }) {
    const { removeFromCart, updateQuantity } = useCart();

    return (
        <div className="cart-item">
            <div className="cart-item-delete-button" onClick={() => removeFromCart(cartItem.productId)}>
                X
            </div>
            <div className="cart-item-image">
                <img src={`${import.meta.env.VITE_IMAGES_BASE_URL}${cartItem.productImage}`} alt={cartItem.productName} />
            </div>
            <div className="cart-item-name">{cartItem.productName}</div>
            <div className="cart-item-price">${formatCOP(cartItem.unitPriceAtAdd)}</div>
            <div className="cart-item-quantity">
                <button className="cart-item-qty-btn"
                  onClick={() => {
                    if (cartItem.quantity <= 1) {
                        removeFromCart(cartItem.productId);
                    }
                    else updateQuantity(cartItem.productId, cartItem.quantity - 1);
                  }}>
                    -
                </button>
                <span className="cart-item-qty-value">{cartItem.quantity}</span>
                <button className="cart-item-qty-btn"
                  onClick={() => updateQuantity(cartItem.productId, cartItem.quantity + 1)}>
                    +
                </button>
            </div>
            <div className="cart-item-total">${formatCOP(cartItem.unitPriceAtAdd * cartItem.quantity)}</div>
        </div>
    );
}

export default CartItemCard;