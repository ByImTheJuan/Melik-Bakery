import { useEffect, useState } from "react";

export default function useScrollState(threshold = 25) {
  const [scrolled, setScrolled] = useState(false);

  useEffect(() => {
    const handleScroll = () => {
      setScrolled(window.scrollY > threshold);
    };

    window.addEventListener("scroll", handleScroll);

    return () => window.removeEventListener("scroll", handleScroll);
  }, [threshold]);

  return scrolled;
}