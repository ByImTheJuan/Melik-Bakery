import { useEffect, useMemo, useState } from "react";
import {
  getAllOrders,
  updateOrderStatus,
  ORDER_STATUS_OPTIONS,
  ORDER_STATUS_LABELS,
} from "../../services/orderService";
import { formatCOP } from "../../utils/formatPrice";
import "../../styles/ordersAdmin.css";

function formatDate(dateValue) {
  return new Intl.DateTimeFormat("es-CO", {
    dateStyle: "medium",
    timeStyle: "short",
  }).format(new Date(dateValue));
}

export default function OrdersAdmin() {
  const [orders, setOrders] = useState([]);
  const [selectedOrderId, setSelectedOrderId] = useState(null);
  const [status, setStatus] = useState("loading");
  const [errorMessage, setErrorMessage] = useState("");
  const [submitMessage, setSubmitMessage] = useState("");
  const [isUpdating, setIsUpdating] = useState(false);
  const [selectedStatus, setSelectedStatus] = useState("CREATED");

  const selectedOrder = useMemo(
    () => orders.find((order) => order.id === selectedOrderId) ?? null,
    [orders, selectedOrderId]
  );

  useEffect(() => {
    loadOrders();
  }, []);

  useEffect(() => {
    if (selectedOrder) {
      setSelectedStatus(selectedOrder.status);
    }
  }, [selectedOrder]);

  async function loadOrders() {
    setStatus("loading");
    setErrorMessage("");

    try {
      const data = await getAllOrders();
      setOrders(data);
      setSelectedOrderId((current) => current ?? data[0]?.id ?? null);
      setStatus("success");
    } catch (error) {
      setErrorMessage(
        error.response?.data?.message ??
          "No se pudieron cargar los pedidos."
      );
      setStatus("error");
    }
  }

  async function handleStatusSubmit(event) {
    event.preventDefault();

    if (!selectedOrder) {
      return;
    }

    setIsUpdating(true);
    setErrorMessage("");
    setSubmitMessage("");

    try {
      const updatedOrder = await updateOrderStatus(selectedOrder.id, selectedStatus);

      setOrders((current) =>
        current.map((order) => (order.id === updatedOrder.id ? updatedOrder : order))
      );
      setSubmitMessage("Estado del pedido actualizado correctamente.");
    } catch (error) {
      const apiErrors = error.response?.data?.errors;
      const apiMessage = error.response?.data?.message;

      setErrorMessage(
        apiErrors?.[0] ??
          apiMessage ??
          "No se pudo actualizar el estado del pedido."
      );
    } finally {
      setIsUpdating(false);
    }
  }

  return (
    <section className="orders-admin">
      <div className="orders-admin-header">
        <div>
          <span className="orders-admin-eyebrow">Gestión de pedidos</span>
          <h2>Pedidos registrados</h2>
          <p>
            Consulta el resumen de cada pedido, revisa su contenido completo y actualiza el estado de preparación o entrega.
          </p>
        </div>
      </div>

      {(errorMessage || submitMessage) && (
        <div className={errorMessage ? "orders-admin-alert error" : "orders-admin-alert success"}>
          {errorMessage || submitMessage}
        </div>
      )}

      <div className="orders-admin-layout">
        <div className="orders-admin-panel">
          <div className="orders-admin-panel-header">
            <h3>Listado resumido</h3>
            <span>{orders.length} pedidos</span>
          </div>

          {status === "loading" && <p className="orders-admin-empty">Cargando pedidos...</p>}
          {status === "error" && <p className="orders-admin-empty">{errorMessage}</p>}

          {status === "success" && orders.length === 0 && (
            <p className="orders-admin-empty">Todavía no hay pedidos registrados.</p>
          )}

          {status === "success" && orders.length > 0 && (
            <div className="orders-admin-list">
              {orders.map((order) => (
                <button
                  key={order.id}
                  type="button"
                  className={
                    selectedOrderId === order.id
                      ? "orders-admin-list-item active"
                      : "orders-admin-list-item"
                  }
                  onClick={() => {
                    setSelectedOrderId(order.id);
                    setSubmitMessage("");
                    setErrorMessage("");
                  }}
                >
                  <div className="orders-admin-list-top">
                    <strong>#{order.id}</strong>
                    <span className={`orders-admin-status status-${order.status.toLowerCase()}`}>
                      {ORDER_STATUS_LABELS[order.status]}
                    </span>
                  </div>

                  <p>
                    {order.clientFirstName} {order.clientLastName}
                  </p>

                  <div className="orders-admin-list-meta">
                    <span>{formatDate(order.createdAt)}</span>
                    <span>${formatCOP(order.totalAmount)}</span>
                  </div>
                </button>
              ))}
            </div>
          )}
        </div>

        <div className="orders-admin-panel orders-admin-detail-panel">
          <div className="orders-admin-panel-header">
            <h3>Detalle del pedido</h3>
            {selectedOrder && <span>#{selectedOrder.id}</span>}
          </div>

          {!selectedOrder && (
            <p className="orders-admin-empty">
              Selecciona un pedido para ver toda su información.
            </p>
          )}

          {selectedOrder && (
            <div className="orders-admin-detail">
              <div className="orders-admin-summary-grid">
                <div>
                  <span className="orders-admin-label">Cliente</span>
                  <strong>
                    {selectedOrder.clientFirstName} {selectedOrder.clientLastName}
                  </strong>
                </div>

                <div>
                  <span className="orders-admin-label">Correo</span>
                  <strong>{selectedOrder.clientEmail}</strong>
                </div>

                <div>
                  <span className="orders-admin-label">Teléfono</span>
                  <strong>{selectedOrder.clientPhoneNumber}</strong>
                </div>

                <div>
                  <span className="orders-admin-label">Creado</span>
                  <strong>{formatDate(selectedOrder.createdAt)}</strong>
                </div>

                <div>
                  <span className="orders-admin-label">Estado actual</span>
                  <strong>{ORDER_STATUS_LABELS[selectedOrder.status]}</strong>
                </div>

                <div>
                  <span className="orders-admin-label">Total</span>
                  <strong>${formatCOP(selectedOrder.totalAmount)}</strong>
                </div>
              </div>

              <div className="orders-admin-section">
                <h4>Dirección de envío</h4>
                <p>
                  {selectedOrder.shippingAddress.street}
                  {selectedOrder.shippingAddress.additionalInformation
                    ? `, ${selectedOrder.shippingAddress.additionalInformation}`
                    : ""}
                </p>
                <p>
                  {selectedOrder.shippingAddress.city}, {selectedOrder.shippingAddress.country}{" "}
                  {selectedOrder.shippingAddress.zipCode}
                </p>
              </div>

              <div className="orders-admin-section">
                <h4>Productos del pedido</h4>
                <div className="orders-admin-items">
                  {selectedOrder.items.map((item) => (
                    <article className="orders-admin-item" key={item.id}>
                      <div>
                        <strong>{item.productName}</strong>
                        <p>Cantidad: {item.quantity}</p>
                      </div>
                      <span>${formatCOP(item.unitPriceAtPurchase)}</span>
                    </article>
                  ))}
                </div>
              </div>

              <form className="orders-admin-status-form" onSubmit={handleStatusSubmit}>
                <label>
                  <span className="orders-admin-label">Cambiar estado</span>
                  <select
                    value={selectedStatus}
                    onChange={(event) => setSelectedStatus(event.target.value)}
                  >
                    {ORDER_STATUS_OPTIONS.map((statusOption) => (
                      <option key={statusOption} value={statusOption}>
                        {ORDER_STATUS_LABELS[statusOption]}
                      </option>
                    ))}
                  </select>
                </label>

                <button
                  type="submit"
                  className="orders-admin-primary"
                  disabled={isUpdating}
                >
                  {isUpdating ? "Actualizando..." : "Guardar estado"}
                </button>
              </form>
            </div>
          )}
        </div>
      </div>
    </section>
  );
}
