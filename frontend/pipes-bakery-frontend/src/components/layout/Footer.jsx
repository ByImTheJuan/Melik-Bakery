import {
  FaEnvelope,
  FaInstagram,
  FaMapMarkerAlt,
  FaTiktok,
  FaWhatsapp
} from "react-icons/fa";
import { Link } from "react-router-dom";

import useInView from "../../hooks/useInView";

const contactLinks = [
  {
    label: "WhatsApp",
    value: "+57 319 383 0446",
    href: "https://wa.me/573193830446",
    icon: <FaWhatsapp />,
    external: true,
  },
  {
    label: "Correo electrónico",
    value: "melik.bakery@hyd.net.co",
    href: "mailto:melik.bakery@hyd.net.co",
    icon: <FaEnvelope />,
  },
];

const socialLinks = [
  {
    label: "Instagram",
    value: "@melik.bakery",
    href: "https://instagram.com/melik.bakery",
    icon: <FaInstagram />,
    external: true,
  },
  {
    label: "TikTok",
    value: "@melik.bakery",
    href: "https://tiktok.com/@melik.bakery",
    icon: <FaTiktok />,
    external: true,
  },
];

function FooterLinkList({ links }) {
  return (
    <ul className="footer-links">
      {links.map(({ label, value, href, icon, external }) => (
        <li key={href}>
          <a
            href={href}
            className="footer-link"
            {...(external && { target: "_blank", rel: "noopener noreferrer" })}
          >
            <span className="footer-link-icon" aria-hidden="true">
              {icon}
            </span>
            <span className="footer-link-text">
              <span className="footer-link-label">{label}</span>
              <span className="footer-link-value">{value}</span>
            </span>
          </a>
        </li>
      ))}
    </ul>
  );
}

export default function Footer() {

  const [ref, isVisible] = useInView();

  return (
    <footer
      id="contact"
      ref={ref}
      className={`footer ${isVisible ? "show" : ""}`}
    >

      <div className="footer-ornament" aria-hidden="true">
        <span className="footer-ornament-line" />
        <span className="footer-ornament-diamond" />
        <span className="footer-ornament-line" />
      </div>

      <div className="footer-main">

        <div className="footer-brand">
          <img
            src="/images/logo.webp"
            alt="Melik Bakery"
            className="footer-logo"
            width="688"
            height="658"
            loading="lazy"
          />
          <p className="footer-location">
            <FaMapMarkerAlt aria-hidden="true" />
            <span>Bogotá, Colombia</span>
          </p>
        </div>

        <section className="footer-column" aria-labelledby="footer-contact-title">
          <h2 id="footer-contact-title">Contáctanos</h2>
          <FooterLinkList links={contactLinks} />
        </section>

        <section className="footer-column" aria-labelledby="footer-social-title">
          <h2 id="footer-social-title">Síguenos en redes sociales</h2>
          <FooterLinkList links={socialLinks} />
        </section>

      </div>

      <div className="footer-bottom">
        <div className="footer-bottom-inner">
          <p className="footer-copy">
            © {new Date().getFullYear()} Melik Bakery
          </p>

          <nav className="footer-legal" aria-label="Información legal">
            <Link to="/politica-de-privacidad">Política de Privacidad</Link>
            <Link to="/politica-de-cookies">Política de Cookies</Link>
          </nav>
        </div>
      </div>

    </footer>
  );
}
