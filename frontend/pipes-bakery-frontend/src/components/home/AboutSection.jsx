import useInView from "../../hooks/useInView";
import { useEffect, useRef } from "react";


export default function AboutSection() {
    const [ref, isVisible] = useInView();
    const imageRef = useRef(null);

    useEffect(() => {
        const handleScroll = () => {
        if (!imageRef.current) return;
        if (window.innerWidth < 768) return;

        const rect = imageRef.current.getBoundingClientRect();
        const offset = rect.top * 0.1; // intensidad suave

        imageRef.current.style.transform = `translateY(${offset}px)`;
        };

        window.addEventListener("scroll", handleScroll);
        return () => window.removeEventListener("scroll", handleScroll);
    }, []);

    return (
        <section
            id="about"
            ref={ref}
            className={`about ${isVisible ? "show" : ""}`}
        >
            <div className="about-container">

                <div className={`about-image ${isVisible ? "show" : ""}`}>
                    <img ref={imageRef} src="/images/aboutSection.jpg" alt="Nuestra panadería" />
                </div>

                <div className={`about-content ${isVisible ? "show" : ""}`}>
                    <h2>Quiénes somos</h2>
                    <p>
                        En Pipe’s Bakery creemos en el valor de lo artesanal.
                        Cada pieza de pan es elaborada a mano, respetando los
                        tiempos tradicionales de fermentación y utilizando
                        ingredientes naturales de la más alta calidad.
                    </p>
                    <p>
                        Nuestra pasión es llevar a tu mesa el sabor auténtico
                        de la panadería clásica, con un toque contemporáneo.
                    </p>
                </div>

            </div>
        </section>
    );
}
