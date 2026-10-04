import { fireEvent, render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { MemoryRouter } from "react-router-dom";
import FaqSection from "./FaqSection";
import { CAKE_FAQ, HOME_FAQ } from "./faqContent";

function renderFaq(items = HOME_FAQ) {
  return render(
    <MemoryRouter>
      <FaqSection id="faq" title="Preguntas frecuentes" items={items} />
    </MemoryRouter>
  );
}

describe("FaqSection", () => {
  it("renders the title and every question collapsed", () => {
    renderFaq();

    expect(screen.getByRole("heading", { level: 2, name: "Preguntas frecuentes" })).toBeInTheDocument();
    for (const { question } of HOME_FAQ) {
      const summary = screen.getByText(question);
      expect(summary.closest("details")).not.toHaveAttribute("open");
    }
  });

  it("opens an answer when its question is clicked", () => {
    renderFaq(CAKE_FAQ);

    const question = screen.getByText("¿Qué son los pisos decorativos?");
    fireEvent.click(question.closest("summary"));

    expect(question.closest("details")).toHaveAttribute("open");
  });

  it("offers a WhatsApp contact for anything not covered", () => {
    renderFaq();

    expect(screen.getByRole("link", { name: /Escríbenos por WhatsApp/ })).toHaveAttribute(
      "href",
      "https://wa.me/573193830446"
    );
  });
});

describe("FAQ content", () => {
  it("has 4-5 questions per section, sharing some but not all", () => {
    const home = HOME_FAQ.map((item) => item.question);
    const cake = CAKE_FAQ.map((item) => item.question);
    const shared = home.filter((question) => cake.includes(question));

    for (const list of [home, cake]) {
      expect(list.length).toBeGreaterThanOrEqual(4);
      expect(list.length).toBeLessThanOrEqual(5);
      expect(new Set(list).size).toBe(list.length);
    }
    expect(shared.length).toBeGreaterThan(0);
    expect(shared.length).toBeLessThan(Math.min(home.length, cake.length));
  });
});
