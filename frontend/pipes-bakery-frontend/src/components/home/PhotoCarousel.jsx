import { useState } from "react";

const images = [
  "/images/homePageCarousel1.jpg",
  "/images/homePageCarousel2.jpg",
  "/images/homePageCarousel3.jpg"
];

export default function PhotoCarousel() {
  const [currentIndex, setCurrentIndex] = useState(0);

  function nextImage() {
    setCurrentIndex((prev) =>
      prev === images.length - 1 ? 0 : prev + 1
    );
  }

  function prevImage() {
    setCurrentIndex((prev) =>
      prev === 0 ? images.length - 1 : prev - 1
    );
  }

  return (
    <section className="carousel">
      <button className="carousel-btn left" onClick={prevImage}>◀</button>
      <img src={images[currentIndex]} alt="Bakery" />
      <button className="carousel-btn right" onClick={nextImage}>▶</button>
    </section>
  );
}