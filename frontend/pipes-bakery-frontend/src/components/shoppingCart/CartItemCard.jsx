import { LuCakeSlice } from "react-icons/lu";
import { useCart } from "../../hooks/useCart";
import { formatCOP } from "../../utils/formatPrice";
import { getProductImageUrl } from "../../utils/productImage";
import { describeCustomCake, isCustomCake } from "../../utils/customCake";
import "../../styles/global.css";


function CartItemCard({ cartItem }) {
    const { removeFromCart, updateQuantity, removeCustomCake, updateCustomCakeQuantity } = useCart();
    const customCake = isCustomCake(cartItem);

    // Las tortas personalizadas se identifican por lineId; los productos por productId
    const remove = () => customCake ? removeCustomCake(cartItem.lineId) : removeFromCart(cartItem.productId);
    const changeQuantity = (quantity) => customCake
        ? updateCustomCakeQuantity(cartItem.lineId, quantity)
        : updateQuantity(cartItem.productId, quantity);

    return (
        <div className="cart-item">
            <div className="cart-item-delete-button" onClick={remove}>
                X
            </div>
            <div className="cart-item-image">
                {cartItem.productImage ? (
                    <img src={getProductImageUrl(cartItem.productImage)} alt={cartItem.productName} />
                ) : (
                    <div className="cart-item-cake-icon" aria-hidden="true">
                        <LuCakeSlice />
                    </div>
                )}
            </div>
            <div className="cart-item-name">
                {cartItem.productName}
                {customCake && (
                    <span className="cart-item-detail">{describeCustomCake(cartItem.customCake)}</span>
                )}
            </div>
            <div className="cart-item-price">${formatCOP(cartItem.unitPriceAtAdd)}</div>
            <div className="cart-item-quantity">
                <button className="cart-item-qty-btn"
                  onClick={() => {
                    if (cartItem.quantity <= 1) {
                        remove();
                    }
                    else changeQuantity(cartItem.quantity - 1);
                  }}>
                    -
                </button>
                <span className="cart-item-qty-value">{cartItem.quantity}</span>
                <button className="cart-item-qty-btn"
                  onClick={() => changeQuantity(cartItem.quantity + 1)}>
                    +
                </button>
            </div>
            <div className="cart-item-total">${formatCOP(cartItem.unitPriceAtAdd * cartItem.quantity)}</div>
        </div>
    );
}

export default CartItemCard;
