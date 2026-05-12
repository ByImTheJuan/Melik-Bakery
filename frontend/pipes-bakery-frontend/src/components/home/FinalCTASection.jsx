import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import useInView from "../../hooks/useInView";

const images = [
  "/images/homePageCarousel16.jpeg",
  "/images/homePageCarousel15.png",
  "/images/homePageCarousel14.jpeg",
  "/images/homePageCarousel13.jpeg",
  "/images/homePageCarousel12.png",
  "/images/homePageCarousel11.png",
  "/images/homePageCarousel10.jpeg",
  "/images/homePageCarousel9.png",
  "/images/homePageCarousel8.png",
  "/images/homePageCarousel7.jfif",
  "/images/homePageCarousel6.jfif",
  "/images/homePageCarousel5.jpeg",
  "/images/homePageCarousel4.png",
  "/images/homePageCarousel3.jfif",
  "/images/homePageCarousel2.png",
  "/images/homePageCarousel1.jpg",
];

export default function FinalCTASection() {
  const [index, setIndex] = useState(0);
  const [prevIndex, setPrevIndex] = useState(0);
  const [ref, isVisible] = useInView();
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
    <section className="final-cta-container">
      <div
        ref={ref}
        className={`final-cta ${isVisible ? "show" : ""}`}
      >
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
