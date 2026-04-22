const CheckoutForm = ({ formData, onChange }) => {
  return (
    <div className="checkout-form">
      <h2>Datos de contacto</h2>

      <input
        type="text"
        placeholder="Nombre"
        value={formData.clientSnapshot.name}
        onChange={(e) =>
          onChange("clientSnapshot", "name", e.target.value)
        }
      />

      <input
        type="email"
        placeholder="Email"
        value={formData.clientSnapshot.email}
        onChange={(e) =>
          onChange("clientSnapshot", "email", e.target.value)
        }
      />

      <input
        type="text"
        placeholder="Teléfono"
        value={formData.clientSnapshot.phone}
        onChange={(e) =>
          onChange("clientSnapshot", "phone", e.target.value)
        }
      />

      <h2>Dirección de envío</h2>

      <input
        type="text"
        placeholder="Dirección"
        value={formData.shippingAddress.street}
        onChange={(e) =>
          onChange("shippingAddress", "street", e.target.value)
        }
      />

      <input
        type="text"
        placeholder="Ciudad"
        value={formData.shippingAddress.city}
        onChange={(e) =>
          onChange("shippingAddress", "city", e.target.value)
        }
      />

      <input
        type="text"
        placeholder="Código postal"
        value={formData.shippingAddress.postalCode}
        onChange={(e) =>
          onChange("shippingAddress", "postalCode", e.target.value)
        }
      />

      <input
        type="text"
        placeholder="País"
        value={formData.shippingAddress.country}
        onChange={(e) =>
          onChange("shippingAddress", "country", e.target.value)
        }
      />
    </div>
  );
};

export default CheckoutForm;