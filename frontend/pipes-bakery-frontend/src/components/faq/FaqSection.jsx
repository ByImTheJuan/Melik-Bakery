import { FaWhatsapp } from "react-icons/fa";
import { LuPlus } from "react-icons/lu";
import "../../styles/faqSection.css";

// Accordion built on native <details>: keyboard and screen-reader support come for free,
// and sharing `name` keeps a single answer open at a time where the browser supports it.
export default function FaqSection({ id, title, items, className = "" }) {
  const titleId = `${id}-title`;

  return (
    <section id={id} className={`faq ${className}`.trim()} aria-labelledby={titleId}>
      <div className="faq-inner">
        <div className="faq-intro">
          <h2 id={titleId}>{title}</h2>
          <p>
            ¿No encuentras tu respuesta?{" "}
            <a
              href="https://wa.me/573193830446"
              target="_blank"
              rel="noopener noreferrer"
              className="faq-contact"
            >
              <FaWhatsapp aria-hidden="true" />
              Escríbenos por WhatsApp
            </a>
          </p>
        </div>

        <div className="faq-list">
          {items.map(({ question, answer }) => (
            <details key={question} name={id} className="faq-item">
              <summary>
                <span>{question}</span>
                <LuPlus className="faq-icon" aria-hidden="true" />
              </summary>
              <div className="faq-answer">{answer}</div>
            </details>
          ))}
        </div>
      </div>
    </section>
  );
}
