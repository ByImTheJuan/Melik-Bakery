import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";

const images = [
  "/images/homePageCarousel1.jpg",
  "/images/homePageCarousel2.jpg",
  "/images/homePageCarousel3.jpg"
];

export default function FinalCTASection() {
  const [index, setIndex] = useState(0);
  const [prevIndex, setPrevIndex] = useState(0);
  //const [ref, isVisible] = useInView();
  const navigate = useNavigate();

  useEffect(() => {
    const interval = setInterval(() => {
        setIndex((prev) => {
            setPrevIndex(index);
            return prev === images.length - 1 ? 0 : prev + 1;
        });
    }, 7000);

    return () => clearInterval(interval);
  }, []);

  return (
    <section className="final-cta">
      <div
        className="final-cta-bg prev"
        style={{ backgroundImage: `url(${images[prevIndex]})` }}
      />

      <div
        className="final-cta-bg current"
        style={{ backgroundImage: `url(${images[index]})` }}
      />

      <div className="final-cta-overlay" />

      <div className="final-cta-content">
        <h2>
          Mira nuestra oferta de deliciosos productos frescos
        </h2>

        <button
          className="final-cta-btn"
          onClick={() => navigate("/products")}
        >
          Ver catálogo
        </button>
      </div>
    </section>
  );
}