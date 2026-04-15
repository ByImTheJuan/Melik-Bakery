import { useState, useEffect } from "react";

const images = [
  "/images/homePageCarousel1.jpg",
  "/images/homePageCarousel2.png",
  "/images/homePageCarousel3.jfif",
  "/images/homePageCarousel4.png",
  "/images/homePageCarousel5.jpeg"
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

  // Autoplay
    const [isHovered, setIsHovered] = useState(false);

    useEffect(() => {
    if (isHovered) return;

    const interval = setInterval(nextImage, 4000);
    return () => clearInterval(interval);
    }, [isHovered]);

  return (
    <section className="carousel">
        <div className="carousel-inner"
          onMouseEnter={() => setIsHovered(true)}
          onMouseLeave={() => setIsHovered(false)}>
            <div
                className="carousel-track"
                style={{
                    transform: `translateX(-${currentIndex * 100}%)`
             }}
            >
                {images.map((img, index) => (
                    <img key={index} src={img} alt="Bakery" />
                ))}
            </div>
            <button className="carousel-btn left" onClick={prevImage}>◀</button>

            <button className="carousel-btn right" onClick={nextImage}>▶</button>

            <div className="carousel-dots">
                {images.map((_, index) => (
                    <button
                        key={index}
                        className={`dot ${currentIndex === index ? "active" : ""}`}
                        onClick={() => setCurrentIndex(index)}
                    />
                ))}
            </div>
            
        </div>
    </section>
  );
}