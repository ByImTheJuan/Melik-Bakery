import HeroSection from "../components/home/HeroSection";
import PhotoCarousel from "../components/home/PhotoCarousel";
import AboutSection from "../components/home/AboutSection";
import FinalCTASection from "../components/home/FinalCTASection";

export default function HomePage() {
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