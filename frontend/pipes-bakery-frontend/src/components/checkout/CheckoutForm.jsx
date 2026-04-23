const CheckoutForm = ({ formData, onChange }) => {
  return (
    <div className="checkout-form">
      <h2>Datos de contacto</h2>

      <input
        type="text"
        placeholder="Nombre"
        value={formData.clientFirstName}
        onChange={(e) => onChange("clientFirstName", e.target.value)}
      />

      <input
        type="text"
        placeholder="Apellido"
        value={formData.clientLastName}
        onChange={(e) => onChange("clientLastName", e.target.value)}
      />

      <input
        type="email"
        placeholder="Email"
        value={formData.clientEmail}
        onChange={(e) => onChange("clientEmail", e.target.value)}
      />

      <input
        type="text"
        placeholder="Teléfono"
        value={formData.clientPhoneNumber}
        onChange={(e) => onChange("clientPhoneNumber", e.target.value)}
      />

      <input
        type="text"
        placeholder="Nombre de quien recibe (opcional)"
        value={formData.receiverName}
        onChange={(e) => onChange("receiverName", e.target.value)}
      />

      <h2>Dirección de envío</h2>

      <input
        type="text"
        placeholder="Dirección"
        value={formData.shippingAddress.street}
        onChange={(e) => onChange("shippingAddress", "street", e.target.value)}
      />

      <input
        type="text"
        placeholder="Información adicional (opcional)"
        value={formData.shippingAddress.additionalInformation}
        onChange={(e) =>
          onChange("shippingAddress", "additionalInformation", e.target.value)
        }
      />

      <input
        type="text"
        placeholder="Ciudad"
        value={formData.shippingAddress.city}
        onChange={(e) => onChange("shippingAddress", "city", e.target.value)}
      />

      <input
        type="text"
        inputMode="numeric"
        placeholder="Código postal"
        value={formData.shippingAddress.zipCode}
        onChange={(e) => onChange("shippingAddress", "zipCode", e.target.value)}
      />

      <input
        type="text"
        placeholder="País"
        value={formData.shippingAddress.country}
        onChange={(e) => onChange("shippingAddress", "country", e.target.value)}
      />
    </div>
  );
};

export default CheckoutForm;
