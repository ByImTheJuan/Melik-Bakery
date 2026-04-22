import { useState } from "react";
import { useCart } from "../context/CartContext";
import CheckoutForm from "../components/checkout/CheckoutForm";
import OrderSummary from "../components/checkout/OrderSummary";
import { useNavigate } from "react-router-dom";
import "../styles/checkoutPage.css";

const OrderPage = () => {
  const { cart, cartId, clearCart } = useCart();
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    clientSnapshot: {
      name: "",
      email: "",
      phone: "",
    },
    shippingAddress: {
      street: "",
      city: "",
      postalCode: "",
      country: "",
    },
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleChange = (section, field, value) => {
    setFormData((prev) => ({
      ...prev,
      [section]: {
        ...prev[section],
        [field]: value,
      },
    }));
  };

  const handleCheckout = async () => {
    try {
      setLoading(true);
      setError(null);

      await cartService.checkout(cartId, formData);

      clearCart();
      navigate("/order-confirmation");
    } catch (err) {
      console.error(err);
      setError("Error al procesar el pedido");
    } finally {
      setLoading(false);
    }
  };

  if (!cart) return <p>Cargando carrito...</p>;

  return (
    <div className="order-page">
      <div className="product-back" onClick={() => navigate(`/cart/${cartId}`)}>
        ← Volver al carrito
      </div>

      <div className="order-container">
        
        <div className="order-form">
          <CheckoutForm
            formData={formData}
            onChange={handleChange}
          />
        </div>

        <div>
          <OrderSummary
            cart={cart}
            onCheckout={handleCheckout}
            loading={loading}
            error={error}
          />
        </div>

      </div>
    </div>
  );
};

export default OrderPage;
