import { useEffect, useState } from "react";
import { useLocation, useNavigate, useParams, useSearchParams } from "react-router-dom";
import { getPaymentStatus } from "../services/paymentService";
import { useDocumentTitle } from "../hooks/useDocumentTitle";
import { formatCOP } from "../utils/formatPrice";
import { describeCustomCake, isCustomCake } from "../utils/customCake";
import { formatDeliveryDate, slotLabel, slotPhrase } from "../utils/delivery";
import "../styles/global.css";
import "../styles/orderConfirmationPage.css";

const OrderConfirmationPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { orderId } = useParams();
  const [searchParams] = useSearchParams();
  const reference = searchParams.get("ref");
  useDocumentTitle("Pedido confirmado");

  const [order, setOrder] = useState(location.state?.order ?? null);
  const [loading, setLoading] = useState(!order && Boolean(reference));

  // Navigation state is lost on refresh; recover the order from the payment reference
  useEffect(() => {
    if (order || !reference) return;

    let cancelled = false;

    getPaymentStatus(reference)
      .then((result) => {
        if (!cancelled && result.order?.id === orderId) {
          setOrder(result.order);
        }
      })
      .catch((err) => {
        if (import.meta.env.DEV) {
          console.error(err);
        }
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, [order, reference, orderId]);

  const items = order?.items ?? [];
  const subtotal = items.reduce(
    (sum, item) => sum + Number(item.unitPriceAtPurchase) * item.quantity,
    0
  );
  const shippingCost = Number(order?.shippingCost ?? 0);

  return (
    <div className="order-confirmation-page">
      <div className="order-confirmation-card">
        <div className="order-confirmation-heading">
          <span className="order-confirmation-seal" aria-hidden="true">
            <svg viewBox="0 0 24 24" width="26" height="26" fill="none" stroke="currentColor" strokeWidth="2.25" strokeLinecap="round" strokeLinejoin="round">
              <path d="M5 12.5l4.5 4.5L19 7.5" />
            </svg>
          </span>
          <h1>Tu pedido ha sido completado con éxito</h1>
        </div>
        <p className="order-confirmation-text">
          {order?.deliveryDate
            ? `Ya recibimos tu pago. Prepararemos tu pedido para entregarlo el ${formatDeliveryDate(order.deliveryDate)} ${slotPhrase(order.deliverySlot)}.`
            : "Ya recibimos tu pago y comenzaremos a preparar tu pedido."}
        </p>

        <div className="order-confirmation-details">
          <div>
            <span className="order-confirmation-label">Número de pedido</span>
            <strong className="order-confirmation-order-number">#{orderId}</strong>
          </div>

          {order && (
            <>
              <div>
                <span className="order-confirmation-label">Cliente</span>
                <strong>
                  {order.clientFirstName} {order.clientLastName}
                </strong>
              </div>

              <div>
                <span className="order-confirmation-label">Email</span>
                <strong className="order-confirmation-email">{order.clientEmail}</strong>
              </div>

              {order.deliveryDate && (
                <div>
                  <span className="order-confirmation-label">Entrega</span>
                  <strong>
                    {formatDeliveryDate(order.deliveryDate, { withYear: false })} · {slotLabel(order.deliverySlot)}
                  </strong>
                </div>
              )}
            </>
          )}
        </div>

        {loading && (
          <p className="order-confirmation-note">Cargando el detalle de tu pedido...</p>
        )}

        {order && (
          <section className="order-confirmation-summary" aria-label="Detalle del pedido">
            <h2>Detalle del pedido</h2>

            <ul className="order-confirmation-items">
              {items.map((item) => (
                <li key={item.id ?? item.productId} className="order-confirmation-item">
                  <div>
                    <span className="order-confirmation-item-name">{item.productName}</span>
                    {isCustomCake(item) && (
                      <span className="order-confirmation-item-meta">{describeCustomCake(item.customCake)}</span>
                    )}
                    <span className="order-confirmation-item-meta">
                      {item.quantity} × ${formatCOP(item.unitPriceAtPurchase)}
                    </span>
                  </div>
                  <strong>${formatCOP(Number(item.unitPriceAtPurchase) * item.quantity)}</strong>
                </li>
              ))}
            </ul>

            <div className="order-confirmation-totals">
              <div>
                <span>Subtotal</span>
                <span>${formatCOP(subtotal)}</span>
              </div>
              <div>
                <span>Gastos de envío</span>
                <span>{shippingCost === 0 ? "Gratis" : `$${formatCOP(shippingCost)}`}</span>
              </div>
              <div className="order-confirmation-grand-total">
                <span>Total pagado</span>
                <span>${formatCOP(order.totalAmount)}</span>
              </div>
            </div>
          </section>
        )}

        <p className="order-confirmation-note">
          Si necesitamos alguna aclaración sobre la entrega, nos pondremos en contacto contigo.
        </p>

        <div className="order-confirmation-actions">
          <button
            className="order-confirmation-primary"
            onClick={() => navigate("/")}
          >
            Volver al inicio
          </button>

          <button
            className="order-confirmation-secondary"
            onClick={() => navigate("/products")}
          >
            Seguir comprando
          </button>
        </div>
      </div>
    </div>
  );
};

export default OrderConfirmationPage;
