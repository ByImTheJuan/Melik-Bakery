import { Link } from "react-router-dom";
import { useDocumentTitle } from "../hooks/useDocumentTitle";
import "../styles/legalPage.css";

const CONTACT_EMAIL = "melik.bakery@hyd.net.co";

const storageItems = [
  {
    name: "XSRF-TOKEN",
    type: "Cookie propia, técnica",
    purpose:
      "Protege los formularios y las acciones de la tienda (por ejemplo, confirmar tu pedido) contra peticiones falsificadas desde otros sitios (CSRF).",
    duration: "Sesión: se borra al cerrar el navegador",
  },
  {
    name: "ADMIN_AUTH_TOKEN",
    type: "Cookie propia, técnica",
    purpose:
      "Mantiene iniciada la sesión del personal de la panadería en el panel de administración. Solo se crea al iniciar sesión como administrador; los clientes nunca la reciben.",
    duration: "Hasta cerrar sesión o que caduque la sesión de administración",
  },
  {
    name: "cartId",
    type: "Almacenamiento local (localStorage)",
    purpose:
      "Guarda el identificador de tu carrito para que no pierdas los productos al recargar la página. El contenido del carrito está en nuestro servidor y se elimina 48 horas después de su última modificación.",
    duration: "Hasta que completes la compra, el carrito caduque o borres los datos del navegador",
  },
  {
    name: "productsScrollPosition",
    type: "Almacenamiento de sesión (sessionStorage)",
    purpose:
      "Recuerda en qué punto del catálogo estabas para devolverte allí al volver desde el detalle de un producto.",
    duration: "Se borra al cerrar la pestaña",
  },
];

const CookiePolicyPage = () => {
  useDocumentTitle("Política de Cookies");

  return (
    <main className="legal-page">
      <article className="legal-content">
        <header className="legal-header">
          <h1>Política de Cookies</h1>
          <p className="legal-subtitle">
            Cookies y almacenamiento del navegador en Melik Bakery
          </p>
          <p className="legal-updated">Última actualización: 4 de octubre de 2026</p>
        </header>

        <p>
          Esta política explica qué cookies y otras tecnologías de almacenamiento usa
          la tienda en línea de Melik Bakery, operada por HYD S.A.S. (NIT
          830021364-7), y cómo puedes gestionarlas. Complementa nuestra{" "}
          <Link to="/politica-de-privacidad">Política de Privacidad</Link>.
        </p>

        <section>
          <h2>1. ¿Qué son las cookies?</h2>
          <p>
            Las cookies son pequeños archivos que un sitio web guarda en tu navegador.
            Funciones similares, como el almacenamiento local (localStorage) y el
            almacenamiento de sesión (sessionStorage), permiten guardar pequeñas
            cantidades de información en tu dispositivo. En esta política nos
            referimos a todas ellas.
          </p>
        </section>

        <section>
          <h2>2. Solo usamos lo estrictamente necesario</h2>
          <p>
            <strong>No usamos cookies de analítica, publicidad, redes sociales ni de
            seguimiento de terceros.</strong> Todas las que se describen a continuación
            son propias y técnicas: son necesarias para que la tienda funcione y sea
            segura, no se usan para elaborar perfiles y no se comparten con terceros.
            Por eso no te mostramos un aviso para aceptarlas.
          </p>
        </section>

        <section>
          <h2>3. Qué usamos exactamente</h2>
          <div className="legal-table-wrap">
            <table className="legal-table">
              <thead>
                <tr>
                  <th scope="col">Nombre</th>
                  <th scope="col">Tipo</th>
                  <th scope="col">Finalidad</th>
                  <th scope="col">Duración</th>
                </tr>
              </thead>
              <tbody>
                {storageItems.map((item) => (
                  <tr key={item.name}>
                    <td data-label="Nombre"><code>{item.name}</code></td>
                    <td data-label="Tipo">{item.type}</td>
                    <td data-label="Finalidad">{item.purpose}</td>
                    <td data-label="Duración">{item.duration}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>

        <section>
          <h2>4. Sitios de terceros</h2>
          <p>
            Cuando pagas, te redirigimos a la plataforma de pagos de{" "}
            <strong>Wompi</strong>, que se encuentra en su propio dominio y puede usar
            sus propias cookies, regidas por su política. Lo mismo ocurre si sigues
            nuestros enlaces a WhatsApp, Instagram o TikTok. Nuestra tienda no
            incluye contenido ni scripts de esos servicios.
          </p>
        </section>

        <section>
          <h2>5. Cómo gestionarlas o eliminarlas</h2>
          <p>
            Puedes ver, bloquear o borrar las cookies y los datos de sitios desde la
            configuración de tu navegador:
          </p>
          <ul>
            <li><strong>Chrome:</strong> Configuración › Privacidad y seguridad › Cookies y otros datos de sitios.</li>
            <li><strong>Firefox:</strong> Ajustes › Privacidad y seguridad › Cookies y datos del sitio.</li>
            <li><strong>Safari:</strong> Ajustes › Privacidad › Gestionar datos de sitios web.</li>
            <li><strong>Edge:</strong> Configuración › Cookies y permisos del sitio.</li>
          </ul>
          <p>
            Ten en cuenta que, como son necesarias, si las bloqueas o borras es
            posible que pierdas tu carrito o que no puedas confirmar tu pedido.
          </p>
        </section>

        <section>
          <h2>6. Contacto y cambios</h2>
          <p>
            Si tienes dudas sobre esta política, escríbenos a{" "}
            <a href={`mailto:${CONTACT_EMAIL}`}>{CONTACT_EMAIL}</a>. Si empezamos a usar
            otras cookies, actualizaremos esta página y la fecha de última
            actualización y, si no fueran estrictamente necesarias, te pediremos tu
            consentimiento antes de activarlas.
          </p>
        </section>
      </article>
    </main>
  );
};

export default CookiePolicyPage;
