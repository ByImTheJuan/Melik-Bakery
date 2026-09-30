import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { createPaymentSession, isOrderPayable } from "../services/paymentService";
import { useDocumentTitle } from "../hooks/useDocumentTitle";
import "../styles/global.css";
import "../styles/paymentPendingPage.css";

const PaymentPendingPage = () => {
  const { orderId } = useParams();
  const navigate = useNavigate();
  useDocumentTitle("Confirmando tu pago");

  const [checking, setChecking] = useState(true);
  const [payable, setPayable] = useState(false);
  const [retrying, setRetrying] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    let cancelled = false;

    async function checkPayable() {
      try {
        const value = await isOrderPayable(orderId);
        if (!cancelled) setPayable(value);
      } catch {
        if (!cancelled) setPayable(false);
      } finally {
        if (!cancelled) setChecking(false);
      }
    }

    checkPayable();

    return () => {
      cancelled = true;
    };
  }, [orderId]);

  const handleRetryPayment = async () => {
    try {
      setRetrying(true);
      setError("");
      const { checkoutUrl } = await createPaymentSession(orderId);
      window.location.href = checkoutUrl;
    } catch (err) {
      setError(
        err.response?.data?.message ?? "No se pudo iniciar el pago. Intentalo de nuevo."
      );
      setRetrying(false);
    }
  };

  return (
    <div className="payment-pending-page">
      <div className="payment-pending-card">
        <div className="payment-pending-badge">Pedido registrado</div>
        <h1>Estamos confirmando tu pago</h1>
        <p className="payment-pending-text">
          Numero de pedido: <strong>#{orderId}</strong>
        </p>
        <p className="payment-pending-text">
          Te avisaremos por correo en cuanto el pago quede confirmado.
        </p>

        {error && <p className="payment-pending-error">{error}</p>}

        <div className="payment-pending-actions">
          {!checking && payable && (
            <button
              className="payment-pending-primary"
              onClick={handleRetryPayment}
              disabled={retrying}
            >
              {retrying ? "Redirigiendo..." : "Pagar ahora"}
            </button>
          )}

          <button className="payment-pending-secondary" onClick={() => navigate("/")}>
            Volver al inicio
          </button>
        </div>
      </div>
    </div>
  );
};

export default PaymentPendingPage;
