import { useState, useEffect } from "react";

// lgWidth is the real pixel width of the -lg file (scripts/optimize-images.mjs never upscales)
const images = [
  { name: "carousel-1", lgWidth: 1280 },
  { name: "carousel-2", lgWidth: 1024 },
  { name: "carousel-3", lgWidth: 1024 },
  { name: "carousel-4", lgWidth: 1024 },
  { name: "carousel-5", lgWidth: 1024 },
  { name: "carousel-6", lgWidth: 1024 },
  { name: "carousel-7", lgWidth: 1024 },
  { name: "carousel-8", lgWidth: 1024 },
];

const SIZES = "(max-width: 1024px) 100vw, 60vw";

export default function PhotoCarousel() {
  const [currentIndex, setCurrentIndex] = useState(0);
  // Slides get their image only once visited (plus the next one), so the first
  // slide, the page's LCP element, doesn't compete with the other seven on load
  const [visited, setVisited] = useState(() => new Set([0]));

  useEffect(() => {
    setVisited((prev) => (prev.has(currentIndex) ? prev : new Set(prev).add(currentIndex)));
  }, [currentIndex]);

  // The neighbour is fetched after the page has loaded, ready for the first slide change
  const [preloadNext, setPreloadNext] = useState(false);

  useEffect(() => {
    if (document.readyState === "complete") {
      setPreloadNext(true);
      return undefined;
    }

    const onLoad = () => setPreloadNext(true);
    window.addEventListener("load", onLoad);
    return () => window.removeEventListener("load", onLoad);
  }, []);

  function shouldLoad(index) {
    if (visited.has(index)) return true;
    return preloadNext && index === (currentIndex + 1) % images.length;
  }

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

    const interval = setInterval(nextImage, 5000);
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
                {images.map((img, index) => {
                    const load = shouldLoad(index);

                    return (
                        <img
                            key={img.name}
                            src={load ? `/images/${img.name}-lg.webp` : undefined}
                            srcSet={load ? `/images/${img.name}-sm.webp 800w, /images/${img.name}-lg.webp ${img.lgWidth}w` : undefined}
                            sizes={SIZES}
                            alt={load ? "Bakery" : ""}
                            decoding="async"
                            fetchPriority={index === 0 ? "high" : "auto"}
                        />
                    );
                })}
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
