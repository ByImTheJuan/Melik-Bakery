import { MAX_DELIVERY_LEAD_DAYS, MIN_DELIVERY_LEAD_DAYS } from "../../utils/delivery";

// Answers describe how the shop really works; keep them in sync with the delivery rules,
// the flat shipping cost (ShoppingCart) and the options in the backend CustomCakeCatalog.

const leadTime = {
  question: "¿Con cuánta anticipación debo hacer mi pedido?",
  answer: (
    <p>
      Horneamos cada pedido para el día de la entrega, así que necesitamos al menos{" "}
      <strong>{MIN_DELIVERY_LEAD_DAYS} días</strong> de anticipación. Puedes programarlo hasta{" "}
      {MAX_DELIVERY_LEAD_DAYS} días antes. Al finalizar tu compra eliges la fecha y la franja de
      entrega: mañana o tarde.
    </p>
  ),
};

const shipping = {
  question: "¿Hacen envíos? ¿Cuánto cuestan?",
  answer: (
    <p>
      Por ahora entregamos únicamente en <strong>Bogotá</strong>. El envío tiene una tarifa fija
      de $10.000, que verás sumada en tu carrito antes de pagar.
    </p>
  ),
};

const payment = {
  question: "¿Cómo pago mi pedido?",
  answer: (
    <p>
      El pago se hace en línea a través de <strong>Wompi</strong>, la pasarela de pagos de
      Bancolombia. Tu pedido queda confirmado en el momento en que el pago es aprobado y, si algo falla,
      puedes reintentarlo. Nosotros nunca vemos ni guardamos los datos de tu tarjeta.
    </p>
  ),
};

const tracking = {
  question: "¿Necesito crear una cuenta? ¿Cómo sigo mi pedido?",
  answer: (
    <p>
      No necesitas cuenta: compras como invitado con tu nombre, correo y dirección. Te
      escribimos por correo cuando se confirma el pago y cada vez que el estado de tu pedido avanza: en
      preparación, enviado y entregado.
    </p>
  ),
};

const changes = {
  question: "¿Puedo cambiar o cancelar mi pedido?",
  answer: (
    <p>
      Escríbenos por WhatsApp al{" "}
      <a href="https://wa.me/573193830446" target="_blank" rel="noopener noreferrer">
        +57 319 383 0446
      </a>{" "}
      lo antes posible. Revisamos cada caso y, si aún no hemos empezado a prepararlo, buscamos
      contigo una solución.
    </p>
  ),
};

const sizes = {
  question: "¿Qué tamaño elijo?",
  answer: (
    <>
      <p>Depende de cuántas personas vayan a comer:</p>
      <ul>
        <li><strong>Pequeña</strong> (15 cm): para unas 8 personas.</li>
        <li><strong>Mediana</strong> (20 cm): para unas 16 personas.</li>
        <li><strong>Grande</strong> (dos pisos reales de 20 y 15 cm): para unas 24 personas.</li>
      </ul>
    </>
  ),
};

const decorativeTiers = {
  question: "¿Qué son los pisos decorativos?",
  answer: (
    <p>
      Son hasta dos pisos falsos, <strong>no comestibles</strong>, que colocamos debajo de tu
      torta para darle altura y presencia en la mesa. Lucen como parte de la torta, pero no
      añaden porciones.
    </p>
  ),
};

const dietary = {
  question: "Tengo alergias o restricciones alimentarias, ¿pueden adaptar la torta?",
  answer: (
    <p>
      Sí. En el primer paso te preguntamos por ellas: cuéntanos cuáles son y adaptaremos la
      receta a lo que nos indiques. Si necesitamos más información o que tomes alguna decisión
      sobre la receta, te contactaremos antes de prepararla.
    </p>
  ),
};

const photo = {
  question: "¿Puedo poner una foto en la torta?",
  answer: (
    <p>
      Sí. En el paso de decoración puedes subir una imagen en JPG, PNG o WEBP y la imprimimos
      sobre la torta con impresión comestible. Usa una foto que tengas derecho a usar y, si
      aparecen otras personas, que cuente con su permiso.
    </p>
  ),
};

export const HOME_FAQ = [leadTime, shipping, payment, tracking, changes];

export const CAKE_FAQ = [leadTime, sizes, decorativeTiers, dietary, photo];
