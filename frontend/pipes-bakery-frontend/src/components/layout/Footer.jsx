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
            href="https://wa.me/573193830446"
            target="_blank"
            rel="noopener noreferrer"
            className="footer-contact-item"
          >
            <FaWhatsapp />
            <span>+57 319 383 0446</span>
          </a>

          <a
            href="mailto:melik.bakery@hyd.net.co"
            className="footer-contact-item"
          >
            <MdEmail />
            <span>melik.bakery@hyd.net.co</span>
          </a>

          <a
            href="https://instagram.com/melik.bakery"
            target="_blank"
            rel="noopener noreferrer"
            className="footer-contact-item"
          >
            <FaInstagram />
            <span>@melik.bakery</span>
          </a>

          <a
            href="https://tiktok.com/@melik.bakery"
            target="_blank"
            rel="noopener noreferrer"
            className="footer-contact-item"
          >
            <FaTiktok />
            <span>@melik.bakery</span>
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