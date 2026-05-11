import {
  FaInstagram,
  FaTiktok,
  FaWhatsapp,
  FaMapMarkerAlt
} from "react-icons/fa";

import { MdEmail } from "react-icons/md";

import useInView from "../../hooks/useInView";

export default function Footer() {

  const [ref, isVisible] = useInView();

  return (
    <footer
      id="contact"
      ref={ref}
      className={`footer ${isVisible ? "show" : ""}`}
    >

      <div className="footer-content">

        <h3>Contáctanos</h3>

        <div className="footer-contact-list">
          <a
            href="https://wa.me/573192908054"
            target="_blank"
            rel="noopener noreferrer"
            className="footer-contact-item"
          >
            <FaWhatsapp />
            <span>+57 319 290 8054</span>
          </a>

          <a
            href="mailto:contacto@melikbakery.com"
            className="footer-contact-item"
          >
            <MdEmail />
            <span>contacto@melikbakery.com</span>
          </a>

          <a
            href="https://instagram.com/melikbakery"
            target="_blank"
            rel="noopener noreferrer"
            className="footer-contact-item"
          >
            <FaInstagram />
            <span>@melikbakery</span>
          </a>

          <a
            href="https://tiktok.com/@melikbakery"
            target="_blank"
            rel="noopener noreferrer"
            className="footer-contact-item"
          >
            <FaTiktok />
            <span>@melikbakery</span>
          </a>
          
        </div>

        <div className="footer-location">
          <FaMapMarkerAlt />
          <span>Bogotá, Colombia</span>
        </div>

        <p className="footer-copy">
          © {new Date().getFullYear()} Melik Bakery
        </p>

      </div>

    </footer>
  );
}