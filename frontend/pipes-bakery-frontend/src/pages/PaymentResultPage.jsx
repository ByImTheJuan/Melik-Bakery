import { useEffect, useState } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import { getPaymentStatus, retryPayment } from "../services/paymentService";
import { useCart } from "../hooks/useCart";
import DeliveryDialog from "../components/checkout/DeliveryDialog";
import { MIN_DELIVERY_LEAD_DAYS, bakeryToday } from "../utils/delivery";
import "../styles/checkoutPage.css";
import { useDocumentTitle } from "../hooks/useDocumentTitle";
import "../styles/global.css";
import "../styles/paymentResultPage.css";

const POLL_INTERVAL_MS = 2000;
const MAX_POLL_ATTEMPTS = 15;

const PaymentResultPage = () => {
  const { reference } = useParams();
  const [searchParams] = useSearchParams();
  const transactionId = searchParams.get("id");
  const navigate = useNavigate();
  const { cartId, clearCart } = useCart();

  // PENDING while waiting for Wompi, FAILED once declined/voided/errored.
  // APPROVED never renders: it redirects to the order confirmation.
  const [status, setStatus] = useState("PENDING");
  const [retrying, setRetrying] = useState(false);
  const [error, setError] = useState("");
  // Set when the delivery date chosen at checkout is now too close: a new one is needed to pay again
  const [needsNewDate, setNeedsNewDate] = useState(false);

  useDocumentTitle(status === "FAILED" ? "Pago no completado" : "Confirmando tu pago");

  useEffect(() => {
    let cancelled = false;
    let timeoutId;
    let attempts = 0;

    async function poll() {
      attempts += 1;

      try {
        const result = await getPaymentStatus(reference, transactionId);
        if (cancelled) return;

        if (result.status === "APPROVED") {
          clearCart();
          // The reference lets the confirmation page reload the order details after a refresh
          navigate(`/order/success/${result.orderId}?ref=${encodeURIComponent(reference)}`, {
            replace: true,
            state: { order: result.order },
          });
          return;
        }

        if (result.status === "FAILED") {
          setStatus("FAILED");
          return;
        }
      } catch (err) {
        if (import.meta.env.DEV) {
          console.error(err);
        }
      }

      if (!cancelled && attempts < MAX_POLL_ATTEMPTS) {
        timeoutId = setTimeout(poll, POLL_INTERVAL_MS);
      }
    }

    poll();

    return () => {
      cancelled = true;
      clearTimeout(timeoutId);
    };
  }, [reference, transactionId, clearCart, navigate]);

  const handleRetryPayment = async (newDelivery) => {
    try {
      setRetrying(true);
      setError("");
      setNeedsNewDate(false);
      const { checkoutUrl } = await retryPayment(reference, newDelivery);
      window.location.href = checkoutUrl;
    } catch (err) {
      setRetrying(false);
      if (err.response?.status === 422) {
        setNeedsNewDate(true);
        return;
      }
      setError(
        err.response?.data?.message ?? "No se pudo iniciar el pago. Intentalo de nuevo."
      );
    }
  };

  if (status === "FAILED") {
    return (
      <div className="payment-result-page">
        <div className="payment-result-card payment-result-card-failed">
          <div className="payment-result-badge payment-result-badge-failed">Pago no completado</div>
          <h1>El pago no se pudo completar</h1>
          <p className="payment-result-reassurance">
            <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
              <path d="M5 12.5l4.5 4.5L19 7.5" />
            </svg>
            <span>No se realizó ningún cobro y tu pedido no ha sido registrado.</span>
          </p>
          <p className="payment-result-text">
            Puedes intentarlo de nuevo o volver al carrito para revisar tu compra.
          </p>

          {error && <p className="payment-result-error" role="alert">{error}</p>}

          <div className="payment-result-actions">
            <button
              className="payment-result-primary"
              onClick={() => handleRetryPayment()}
              disabled={retrying}
            >
              {retrying ? "Redirigiendo…" : "Reintentar pago"}
            </button>

            <button
              className="payment-result-secondary"
              onClick={() => navigate(cartId ? `/cart/${cartId}` : "/cart")}
            >
              Volver al carrito
            </button>
          </div>
        </div>

        {needsNewDate && (
          <DeliveryDialog
            current={null}
            today={bakeryToday()}
            dismissible
            notice={`La fecha de entrega que elegiste ya no está disponible: necesitamos al menos ${MIN_DELIVERY_LEAD_DAYS} días para preparar tu pedido. Elige una nueva para continuar con el pago.`}
            onConfirm={(delivery) => handleRetryPayment(delivery)}
            onClose={() => setNeedsNewDate(false)}
          />
        )}
      </div>
    );
  }

  return (
    <div className="payment-result-page">
      <div className="payment-result-card">
        <div className="payment-result-badge">Procesando pago</div>
        <h1>Estamos confirmando tu pago</h1>
        <p className="payment-result-text">
          Esto puede tardar unos segundos. No cierres esta página.
        </p>
        <p className="payment-result-text">
          Te avisaremos por correo en cuanto el pago quede confirmado.
        </p>

        <div className="payment-result-actions">
          <button className="payment-result-secondary" onClick={() => navigate("/")}>
            Volver al inicio
          </button>
        </div>
      </div>
    </div>
  );
};

export default PaymentResultPage;
