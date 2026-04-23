import { useLocation, useNavigate, useParams } from "react-router-dom";
import { useDocumentTitle } from "../hooks/useDocumentTitle";
import "../styles/global.css";
import "../styles/orderConfirmationPage.css";

const OrderConfirmationPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { orderId } = useParams();
  useDocumentTitle("Pedido confirmado");

  const order = location.state?.order;

  return (
    <div className="order-confirmation-page">
      <div className="order-confirmation-card">
        <div className="order-confirmation-badge">Pedido confirmado</div>
        <h1>Tu pedido ha sido completado con exito</h1>
        <p className="order-confirmation-text">
          Ya recibimos tu solicitud y comenzaremos a prepararla lo antes posible.
        </p>

        <div className="order-confirmation-details">
          <div>
            <span className="order-confirmation-label">Numero de pedido</span>
            <strong>#{orderId}</strong>
          </div>

          {order?.clientFirstName && (
            <div>
              <span className="order-confirmation-label">Cliente</span>
              <strong>
                {order.clientFirstName} {order.clientLastName}
              </strong>
            </div>
          )}

          {order?.totalAmount && (
            <div>
              <span className="order-confirmation-label">Total</span>
              <strong>${new Intl.NumberFormat("es-CO").format(order.totalAmount)}</strong>
            </div>
          )}
        </div>

        <p className="order-confirmation-note">
          Si necesitamos alguna aclaracion sobre la entrega, nos pondremos en contacto contigo.
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
