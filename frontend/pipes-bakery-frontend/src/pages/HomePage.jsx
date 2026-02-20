import HeroSection from "../components/home/HeroSection";
import PhotoCarousel from "../components/home/PhotoCarousel";

export default function HomePage() {
  return (
    <>

      <div className="home-layout">  
        <HeroSection />
        <PhotoCarousel />
      </div>

    </>
  );
}