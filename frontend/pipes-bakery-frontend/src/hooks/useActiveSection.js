import { useEffect, useState } from "react";

export default function useActiveSection(sectionIds) {

    const [activeSection, setActiveSection] = useState(sectionIds[0]);

    useEffect(() => {
        if (!sectionIds.length) return;
        const observer = new IntersectionObserver(
        (entries) => {
            entries.forEach((entry) => {
            if (entry.isIntersecting) {
                setActiveSection(entry.target.id);
            }
            });
        },
        {
            threshold: 0.6,
            rootMargin: "-80px 0px 0px 0px"
        }
        );

        sectionIds.forEach((id) => {
        const el = document.getElementById(id);
        if (el) observer.observe(el);
        });

        return () => observer.disconnect();
    }, [sectionIds]);

    return activeSection;
}