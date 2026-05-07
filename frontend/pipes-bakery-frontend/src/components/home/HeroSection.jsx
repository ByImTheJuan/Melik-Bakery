import { useNavigate } from "react-router-dom";


export default function HeroSection() {
 const navigate = useNavigate();

  return (
    <section id="hero" className="hero">
        <div className="hero-content">
            <img src="/images/logo.png" alt="Logo" />
            <p>Postres de autor horneados con amor.</p>
            <button onClick={() => {
                navigate("/products");
          }}>
            Qué antojo tienes hoy?
          </button>
        </div>
    </section>
  );
}