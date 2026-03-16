import { FaInstagram, FaTiktok, FaWhatsapp } from "react-icons/fa";
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

            <div className="footer-icons">
            <a href="mailto:contacto@pipesbakery.com" target="_blank" rel="noopener noreferrer">
                <MdEmail />
            </a>

            <a href="https://instagram.com/pipesbakery" target="_blank" rel="noopener noreferrer">
                <FaInstagram />
            </a>

            <a href="https://tiktok.com/@pipesbakery" target="_blank" rel="noopener noreferrer">
                <FaTiktok />
            </a>

            <a href="https://wa.me/1234567890" target="_blank" rel="noopener noreferrer">
                <FaWhatsapp />
            </a>
            </div>

            <p className="footer-copy">
            © {new Date().getFullYear()} Pipe’s Bakery
            </p>
        </div>
        </footer>
    );
}