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
                        <b>Nuestra historia:</b><br />
                        En catalán, Melic significa ombligo; sin embargo, también puede significar raíces o centro de algo. 
                        Melik Bakery nace como un tributo a mis raíces catalanas, tomándolas como origen para compartir mis 
                        creaciones con el público.
                    </p>
                    <p>
                        Mi pasión por la cocina se desarrolló en casa, cuando horneaba ocasionalmente tortas con mi padre. 
                        Lo que empezó como un gesto de amor hacia mis seres queridos, se transformó en un camino con el 
                        proposito de mejorar técnicamente mis preparaciones.
                    </p>
                    <p>
                        En la búsqueda del perfeccionamiento de mis productos, tomé diplomados y cursos en distintos espacios. 
                        Asimismo, emprendí un viaje a Bruselas, donde hice una pasantía en una clásica Boulangerie et 
                        Patisserie con un enfoque moderno.
                    </p>
                    <p>
                        <b>Nuestra Filosofía:</b><br />
                        En Melik, creemos en la Autenticidad. Cada producto que sale de nuestra cocina es el resultado de 
                        procesos honestos y una selección minuciosa de materias primas de alta calidad. Estos dos aspectos, 
                        nos posicionan como una marca que vende productos artesanales. No buscamos solo cocinar productos 
                        deliciosos, sino ser el centro de tus celebraciones.
                    </p>
                    <p>
                        <b>Bienvenidos al origen. Bienvenidos a Melik.</b>
                    </p>
                </div>

            </div>
        </section>
    );
}
