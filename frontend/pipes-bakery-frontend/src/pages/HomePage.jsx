import HeroSection from "../components/home/HeroSection";
import PhotoCarousel from "../components/home/PhotoCarousel";
import AboutSection from "../components/home/AboutSection";
import FinalCTASection from "../components/home/FinalCTASection";
import "../styles/global.css";
import "../styles/homePage.css";

import { ensureCartId } from "../services/cartStorage";


import { useLocation, useNavigate } from "react-router-dom";
import { useEffect } from "react";

export default function HomePage() {
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    async function test() {
      const id = await ensureCartId();
      console.log("Cart:", id);
    }

    test();
  }, []);

  useEffect(() => {
    if (location.state?.scrollTo) {
      const element = document.getElementById(location.state.scrollTo);

      if (element) {
        element.scrollIntoView({ behavior: "smooth" });
      }

      // Limpiar el state para que no se repita
      navigate(location.pathname, { replace: true, state: {} });
    }
  }, [location, navigate]);
  
  
  return (
    <>

      <section className="home-layout">  
        <HeroSection />
        <PhotoCarousel />
      </section>

      <AboutSection />
      <FinalCTASection />

    </>
  );
}