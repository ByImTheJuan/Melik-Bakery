import { useState } from "react";
import { useCart } from "../hooks/useCart";
import CheckoutForm from "../components/checkout/CheckoutForm";
import OrderSummary from "../components/checkout/OrderSummary";
import DeliveryDialog from "../components/checkout/DeliveryDialog";
import DeliveryField from "../components/checkout/DeliveryField";
import { bakeryToday, isDeliveryComplete } from "../utils/delivery";
import { checkoutCart } from "../services/cartService";
import { useNavigate } from "react-router-dom";
import { useDocumentTitle } from "../hooks/useDocumentTitle";
import "../styles/checkoutPage.css";

const initialFormData = {
  clientFirstName: "",
  clientLastName: "",
  clientEmail: "",
  clientPhoneNumber: "",
  receiverName: "",
  shippingAddress: {
    street: "",
    additionalInformation: "",
    city: "Bogotá",
    zipCode: "",
    country: "Colombia",
  },
};

const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const bogotaRegex = /^bogota$/i;
const colombiaRegex = /^colombia$/i;
const zipCodeRegex = /^11\d{4}$/;

const CheckoutPage = () => {
  const { cart, cartId } = useCart();
  const navigate = useNavigate();
  useDocumentTitle("Checkout");

  const [formData, setFormData] = useState(initialFormData);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState([]);
  // Prior, express authorization required by Ley 1581 de 2012 before processing personal data
  const [dataConsent, setDataConsent] = useState(false);
  // Delivery date and time slot: asked for as soon as the customer arrives at checkout
  const [delivery, setDelivery] = useState({ date: null, slot: null });
  const [deliveryDialogOpen, setDeliveryDialogOpen] = useState(true);
  const [today] = useState(bakeryToday);
  const deliveryReady = isDeliveryComplete(delivery, today);

  const handleDeliveryConfirm = (chosen) => {
    setDelivery(chosen);
    setDeliveryDialogOpen(false);
    setErrors([]);
  };

  const handleChange = (section, fieldOrValue, maybeValue) => {
    if (typeof maybeValue === "undefined") {
      setFormData((prev) => ({
        ...prev,
        [section]: fieldOrValue,
      }));
      setErrors([]);
      return;
    }

    setFormData((prev) => ({
      ...prev,
      [section]: {
        ...prev[section],
        [fieldOrValue]: maybeValue,
      },
    }));
    setErrors([]);
  };

  const buildCheckoutPayload = () => ({
    clientFirstName: formData.clientFirstName.trim(),
    clientLastName: formData.clientLastName.trim(),
    clientEmail: formData.clientEmail.trim(),
    clientPhoneNumber: formData.clientPhoneNumber.trim(),
    receiverName: formData.receiverName.trim() || null,
    deliveryDate: delivery.date,
    deliverySlot: delivery.slot,
    shippingAddress: {
      street: formData.shippingAddress.street.trim(),
      additionalInformation:
        formData.shippingAddress.additionalInformation.trim() || null,
      city: formData.shippingAddress.city.trim(),
      zipCode: Number(formData.shippingAddress.zipCode),
      country: formData.shippingAddress.country.trim(),
    },
  });

  const normalizeText = (value) =>
    value.normalize("NFD").replace(/[\u0300-\u036f]/g, "");

  const validateCheckoutForm = () => {
    const validationErrors = [];
    const payload = buildCheckoutPayload();

    if (!deliveryReady) {
      validationErrors.push("Elige la fecha y la franja de entrega.");
    }

    if (!payload.clientFirstName) {
      validationErrors.push("El nombre es obligatorio.");
    }

    if (!payload.clientLastName) {
      validationErrors.push("El apellido es obligatorio.");
    }

    if (!payload.clientEmail) {
      validationErrors.push("El email es obligatorio.");
    } else if (!emailRegex.test(payload.clientEmail)) {
      validationErrors.push("Introduce un email valido.");
    }

    if (!payload.clientPhoneNumber) {
      validationErrors.push("El telefono es obligatorio.");
    }

    if (!payload.shippingAddress.street) {
      validationErrors.push("La direccion es obligatoria.");
    }

    if (!payload.shippingAddress.city) {
      validationErrors.push("La ciudad es obligatoria.");
    } else if (!bogotaRegex.test(normalizeText(payload.shippingAddress.city))) {
      validationErrors.push("Por ahora solo aceptamos envíos a Bogotá.");
    }

    if (!formData.shippingAddress.zipCode.trim()) {
      validationErrors.push("El codigo postal es obligatorio.");
    } else if (!zipCodeRegex.test(formData.shippingAddress.zipCode.trim())) {
      validationErrors.push("El codigo postal debe pertenecer a Bogotá.");
    }

    if (!payload.shippingAddress.country) {
      validationErrors.push("El pais es obligatorio.");
    } else if (!colombiaRegex.test(normalizeText(payload.shippingAddress.country))) {
      validationErrors.push("Por ahora solo aceptamos envíos dentro de Colombia.");
    }

    if (!dataConsent) {
      validationErrors.push("Debes autorizar el tratamiento de tus datos personales para continuar.");
    }

    return validationErrors;
  };

  const getApiErrors = (err) => {
    const apiError = err.response?.data;

    if (Array.isArray(apiError?.details) && apiError.details.length > 0) {
      return apiError.details;
    }

    if (apiError?.message) {
      return [apiError.message];
    }

    return ["No se pudo procesar el pedido. Revisa los datos e intentalo de nuevo."];
  };

  const handleCheckout = async () => {
    const validationErrors = validateCheckoutForm();

    if (validationErrors.length > 0) {
      setErrors(validationErrors);
      if (!deliveryReady) setDeliveryDialogOpen(true);
      return;
    }

    try {
      setLoading(true);
      setErrors([]);

      // No order exists yet: it is created only once Wompi approves the payment,
      // so the cart is kept until then (it is cleared on the payment result page).
      const { checkoutUrl } = await checkoutCart(cartId, buildCheckoutPayload());
      window.location.href = checkoutUrl;
    } catch (err) {
      if (import.meta.env.DEV) {
        console.error(err);
      }
      setErrors(getApiErrors(err));
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
          <DeliveryField
            delivery={delivery}
            missing={!deliveryReady && errors.length > 0}
            onOpen={() => setDeliveryDialogOpen(true)}
          />
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
            errors={errors}
            dataConsent={dataConsent}
            onDataConsentChange={(checked) => {
              setDataConsent(checked);
              setErrors([]);
            }}
          />
        </div>
      </div>

      {deliveryDialogOpen && (
        <DeliveryDialog
          current={delivery}
          today={today}
          onConfirm={handleDeliveryConfirm}
          onClose={() => setDeliveryDialogOpen(false)}
          onBackToCart={() => navigate(`/cart/${cartId}`)}
        />
      )}
    </div>
  );
};

export default CheckoutPage;
