import { Link } from "react-router-dom";
import { useDocumentTitle } from "../hooks/useDocumentTitle";
import "../styles/legalPage.css";

const CONTACT_EMAIL = "melik.bakery@hyd.net.co";

const PrivacyPolicyPage = () => {
  useDocumentTitle("Política de Privacidad");

  return (
    <main className="legal-page">
      <article className="legal-content">
        <header className="legal-header">
          <h1>Política de Privacidad</h1>
          <p className="legal-subtitle">
            Política de Tratamiento de Datos Personales de Melik Bakery
          </p>
          <p className="legal-updated">Última actualización: 4 de octubre de 2026</p>
        </header>

        <p>
          En Melik Bakery respetamos tu privacidad. Esta política explica qué datos
          personales recogemos cuando compras en nuestra tienda en línea, para qué los
          usamos, con quién los compartimos y cómo puedes ejercer tus derechos, de
          acuerdo con la Ley Estatutaria 1581 de 2012, el Decreto 1377 de 2013
          (compilado en el Decreto Único Reglamentario 1074 de 2015) y la Ley 1480 de
          2011 (Estatuto del Consumidor).
        </p>

        <section>
          <h2>1. Responsable del tratamiento</h2>
          <ul className="legal-facts">
            <li><strong>Razón social:</strong> HYD S.A.S. (marca comercial Melik Bakery)</li>
            <li><strong>NIT:</strong> 830021364-7</li>
            <li><strong>Domicilio:</strong> Bogotá D.C., Colombia</li>
            <li>
              <strong>Correo electrónico:</strong>{" "}
              <a href={`mailto:${CONTACT_EMAIL}`}>{CONTACT_EMAIL}</a>
            </li>
            <li>
              <strong>WhatsApp:</strong>{" "}
              <a href="https://wa.me/573193830446" target="_blank" rel="noopener noreferrer">
                +57 319 383 0446
              </a>
            </li>
          </ul>
        </section>

        <section>
          <h2>2. Datos que recogemos</h2>
          <p>
            En nuestra tienda no hay cuentas de cliente: compras como invitado y solo
            te pedimos los datos necesarios para preparar y entregar tu pedido.
          </p>
          <ul>
            <li>
              <strong>Datos de identificación y contacto:</strong> nombre, apellidos,
              correo electrónico y número de teléfono.
            </li>
            <li>
              <strong>Datos de entrega:</strong> dirección, información adicional de la
              dirección, ciudad, código postal, país, nombre de la persona que recibe
              (si es distinta de ti) y la fecha y franja horaria de entrega elegidas.
            </li>
            <li>
              <strong>Datos del pedido:</strong> productos, cantidades, precios, valor
              del envío, estado del pedido y referencia del pago.
            </li>
            <li>
              <strong>Datos de tu torta personalizada:</strong> tamaño, sabor, colores,
              decoración, el texto que quieras escribir en ella, las notas que dejes
              para el pastelero y, si decides subirla, la fotografía para la impresión
              comestible. Te pedimos que solo subas fotografías sobre las que tengas
              derecho y, si aparecen otras personas, que cuentes con su autorización.
            </li>
            <li>
              <strong>Restricciones alimentarias:</strong> si nos indicas que tienes
              alergias, intolerancias u otras restricciones. Esta información puede
              revelar datos relativos a la salud, que la ley considera{" "}
              <strong>datos sensibles</strong> (ver sección 4).
            </li>
            <li>
              <strong>Datos técnicos:</strong> la dirección IP desde la que se suben
              fotografías se usa de forma temporal (como máximo una hora) solo para
              limitar abusos, y no se asocia a tu pedido.
            </li>
          </ul>
          <p>
            <strong>No recogemos ni almacenamos datos de tu tarjeta ni de tu cuenta
            bancaria.</strong> El pago se realiza directamente en la plataforma de
            Wompi; nosotros solo recibimos la confirmación de si el pago fue aprobado o
            rechazado.
          </p>
        </section>

        <section>
          <h2>3. Para qué usamos tus datos</h2>
          <ul>
            <li>Preparar tu pedido, incluida la elaboración de tu torta personalizada.</li>
            <li>Entregarlo en la dirección, fecha y franja que elegiste.</li>
            <li>
              Gestionar el pago a través de Wompi, comprobar su estado y permitirte
              reintentarlo si falla.
            </li>
            <li>
              Enviarte correos electrónicos sobre tu pedido: confirmación de pago y
              cambios de estado (en preparación, enviado, entregado o cancelado).
            </li>
            <li>Contactarte si necesitamos aclarar algo sobre tu pedido o su entrega.</li>
            <li>
              Cumplir nuestras obligaciones legales, contables y tributarias, y atender
              peticiones, quejas, reclamos y garantías.
            </li>
            <li>Proteger la seguridad de la tienda y evitar usos fraudulentos o abusivos.</li>
          </ul>
          <p>
            <strong>No usamos tus datos para publicidad, no te enviamos correos de
            marketing, no elaboramos perfiles y no vendemos ni cedemos tus datos a
            terceros.</strong>
          </p>
        </section>

        <section>
          <h2>4. Datos sensibles</h2>
          <p>
            La información sobre restricciones alimentarias puede constituir un dato
            sensible relativo a la salud. Conforme a los artículos 5 y 6 de la Ley 1581
            de 2012, te informamos que <strong>no estás obligado a suministrarla</strong>.
            Si decides hacerlo, la usaremos únicamente para preparar tu torta de forma
            segura y solo la conocerá el personal que elabora tu pedido. Indicarla de
            forma detallada es opcional; ten en cuenta que, si no lo haces, no podremos
            adaptar la preparación a tus necesidades.
          </p>
        </section>

        <section>
          <h2>5. Con quién compartimos tus datos</h2>
          <p>
            Solo compartimos los datos estrictamente necesarios con los siguientes
            proveedores, que actúan como encargados del tratamiento por cuenta nuestra:
          </p>
          <ul>
            <li>
              <strong>Wompi (Bancolombia S.A.)</strong>, pasarela de pagos en Colombia:
              recibe la referencia y el valor de tu compra, y en su plataforma introduces
              tus datos de pago bajo su propia política de privacidad.
            </li>
            <li>
              <strong>Resend, Inc.</strong>, servicio de envío de correos electrónicos
              ubicado en Estados Unidos: recibe tu correo electrónico, tu nombre y la
              información del pedido incluida en los correos que te enviamos.
            </li>
          </ul>
          <p>
            Por usar el servicio de Resend, tus datos pueden ser objeto de{" "}
            <strong>transferencia o transmisión internacional</strong> a Estados Unidos,
            con las garantías previstas en el artículo 26 de la Ley 1581 de 2012. Al
            otorgar tu autorización aceptas expresamente esta transmisión.
          </p>
          <p>
            La tienda, su base de datos y las fotografías que subes se alojan en
            servidores administrados por HYD S.A.S. También podremos entregar datos a
            autoridades cuando una ley u orden judicial lo exija.
          </p>
          <p>
            Los enlaces a WhatsApp, Instagram y TikTok te llevan a servicios de
            terceros que se rigen por sus propias políticas de privacidad.
          </p>
        </section>

        <section>
          <h2>6. Tus derechos</h2>
          <p>Como titular de los datos, según el artículo 8 de la Ley 1581 de 2012, puedes:</p>
          <ul>
            <li>Conocer, actualizar y rectificar tus datos personales.</li>
            <li>Solicitar prueba de la autorización que nos otorgaste.</li>
            <li>Ser informado sobre el uso que hemos dado a tus datos.</li>
            <li>
              Revocar la autorización y/o solicitar la supresión de tus datos, siempre
              que no exista un deber legal o contractual de conservarlos.
            </li>
            <li>Acceder gratuitamente a tus datos personales.</li>
            <li>
              Presentar quejas ante la Superintendencia de Industria y Comercio (SIC)
              por infracciones a la normativa de protección de datos, una vez agotado
              el trámite de consulta o reclamo ante nosotros.
            </li>
          </ul>
        </section>

        <section>
          <h2>7. Cómo ejercer tus derechos</h2>
          <p>
            Escríbenos a <a href={`mailto:${CONTACT_EMAIL}`}>{CONTACT_EMAIL}</a>{" "}
            indicando tu nombre, el correo con el que hiciste el pedido, tu solicitud
            y, si la tienes, la referencia del pedido. Podremos pedirte información
            adicional para verificar tu identidad.
          </p>
          <ul>
            <li>
              <strong>Consultas:</strong> las responderemos en un máximo de 10 días
              hábiles desde su recibo. Si no fuera posible, te informaremos el motivo y
              la nueva fecha, que no superará 5 días hábiles adicionales.
            </li>
            <li>
              <strong>Reclamos</strong> (corrección, actualización, supresión o
              presunto incumplimiento): los atenderemos en un máximo de 15 días hábiles
              desde su recibo, prorrogables hasta 8 días hábiles más, informándote del
              motivo. Si el reclamo está incompleto, te pediremos subsanarlo dentro de
              los 5 días siguientes; si pasan 2 meses sin respuesta, se entenderá que
              desististe.
            </li>
          </ul>
        </section>

        <section>
          <h2>8. Cuánto tiempo conservamos tus datos</h2>
          <p>
            Conservamos los datos de tus pedidos mientras sean necesarios para las
            finalidades descritas y, después, durante el plazo exigido para cumplir
            nuestras obligaciones contables y tributarias (hasta 10 años, según el
            artículo 60 del Código de Comercio). El contenido de los carritos de compra
            se elimina automáticamente 48 horas después de su última modificación.
          </p>
        </section>

        <section>
          <h2>9. Seguridad</h2>
          <p>
            Aplicamos medidas técnicas y organizativas razonables para proteger tus
            datos: comunicaciones cifradas mediante HTTPS, acceso al panel de
            administración restringido al personal autorizado de la panadería con
            credenciales protegidas, y cookies de sesión configuradas para no ser
            accesibles desde el navegador. Ningún sistema es completamente
            infalible, pero trabajamos para evitar el acceso no autorizado, la pérdida
            o la alteración de tus datos.
          </p>
        </section>

        <section>
          <h2>10. Menores de edad</h2>
          <p>
            Nuestra tienda está dirigida a personas mayores de edad. Si eres menor de
            18 años, tu compra debe realizarla tu padre, madre o representante legal,
            quien otorgará la autorización correspondiente.
          </p>
        </section>

        <section>
          <h2>11. Autorización</h2>
          <p>
            Antes de confirmar tu pedido te pedimos que marques la casilla con la que
            autorizas de forma previa, expresa e informada el tratamiento de tus datos
            conforme a esta política. Sin esa autorización no podemos procesar tu
            pedido. Puedes revocarla en cualquier momento escribiéndonos, en los
            términos de la sección 7.
          </p>
        </section>

        <section>
          <h2>12. Cookies</h2>
          <p>
            Para saber qué cookies y almacenamiento del navegador usamos, consulta
            nuestra <Link to="/politica-de-cookies">Política de Cookies</Link>.
          </p>
        </section>

        <section>
          <h2>13. Vigencia y cambios</h2>
          <p>
            Esta política está vigente desde el 4 de octubre de 2026. Las bases de
            datos estarán vigentes mientras HYD S.A.S. desarrolle su actividad
            comercial. Si hacemos cambios sustanciales, los publicaremos en esta página
            actualizando la fecha de última actualización y, cuando la ley lo exija,
            te pediremos una nueva autorización.
          </p>
        </section>
      </article>
    </main>
  );
};

export default PrivacyPolicyPage;
