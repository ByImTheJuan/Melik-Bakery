import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import useInView from "../../hooks/useInView";

const images = [16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1].map(
  (n) => `/images/carousel-${n}-lg.webp`
);

export default function FinalCTASection() {
  const [index, setIndex] = useState(0);
  const [ref, isVisible] = useInView();
  const navigate = useNavigate();
  const prevIndex =
  index === 0
    ? images.length - 1
    : index - 1;

  // Below the fold: no image is requested until the section scrolls into view
  const backgroundFor = (i) =>
    isVisible ? { backgroundImage: `url(${images[i]})` } : undefined;

  useEffect(() => {
    const interval = setInterval(() => {
        setIndex((prev) => 
          prev === images.length - 1
          ? 0
          : prev + 1
        );
    }, 7000);

    return () => clearInterval(interval);
  }, []);

  return (
    <section className="final-cta-container">
      <div
        ref={ref}
        className={`final-cta ${isVisible ? "show" : ""}`}
      >
        <div
          className="final-cta-bg prev"
          style={backgroundFor(prevIndex)}
        />

        <div
          className="final-cta-bg current"
          style={backgroundFor(index)}
        />

        <div className="final-cta-overlay" />

        <div className="final-cta-content">
          <h2>
            Visita nuestra oferta de deliciosos productos frescos
          </h2>

          <button
            className="final-cta-btn"
            onClick={() => navigate("/products")}
          >
            Ver catálogo de productos
          </button>
        </div>
      </div>
    </section>
  );
}
