import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { LuArrowLeft, LuArrowRight, LuCheck } from "react-icons/lu";
import { useDocumentTitle } from "../hooks/useDocumentTitle";
import { useCart } from "../hooks/useCart";
import { PHASES, useCakeBuilder } from "../hooks/useCakeBuilder";
import { useCustomCakeOptions } from "../hooks/useCustomCakeOptions";
import { uploadCakeImage } from "../services/customCakeService";
import {
  estimateCakePrice,
  formatDecorativeTiers,
  isDietaryAnswerComplete,
  toCakeConfiguration,
} from "../utils/customCake";
import { formatCOP } from "../utils/formatPrice";
import CakeViewer from "../components/personalization/CakeViewer";
import PhaseStepper from "../components/personalization/PhaseStepper";
import BasePhase from "../components/personalization/BasePhase";
import DecorationPhase from "../components/personalization/DecorationPhase";
import CakeSummary from "../components/personalization/CakeSummary";
import FaqSection from "../components/faq/FaqSection";
import { CAKE_FAQ } from "../components/faq/faqContent";

import "../styles/global.css";
import "../styles/personalizationPage.css";

const FIRST_FIELD = ["size", "color", null];

export default function PersonalizationPage() {
  useDocumentTitle("Personaliza tu torta");
  const navigate = useNavigate();
  const { cartId, cart, addCustomCake } = useCart();
  const { options, status, retry } = useCustomCakeOptions();
  const [design, dispatch] = useCakeBuilder();
  const [openField, setOpenField] = useState(FIRST_FIELD[0]);
  const [quantity, setQuantity] = useState(1);
  const [adding, setAdding] = useState(false);
  const [added, setAdded] = useState(false);
  const [image, setImage] = useState({ uploading: false, error: "" });
  const [showDietaryError, setShowDietaryError] = useState(false);
  const headingRef = useRef(null);
  const dietaryRef = useRef(null);
  const previewUrlRef = useRef(null);

  // Free the local photo preview when it is replaced or the page is left
  useEffect(() => () => {
    if (previewUrlRef.current) URL.revokeObjectURL(previewUrlRef.current);
  }, []);

  function goToPhase(phase, field = FIRST_FIELD[phase]) {
    // The dietary question must be answered before leaving the first phase
    if (phase > 0 && !isDietaryAnswerComplete(design)) {
      dispatch({ type: "goToPhase", phase: 0 });
      setShowDietaryError(true);
      requestAnimationFrame(() => dietaryRef.current?.scrollIntoView({ behavior: "smooth", block: "center" }));
      return;
    }

    setShowDietaryError(false);
    dispatch({ type: "goToPhase", phase });
    setOpenField(field);
    setAdded(false);
    // Keep keyboard and screen reader users oriented when the right panel changes
    requestAnimationFrame(() => headingRef.current?.focus({ preventScroll: true }));
  }

  function replacePreview(url) {
    if (previewUrlRef.current) URL.revokeObjectURL(previewUrlRef.current);
    previewUrlRef.current = url;
  }

  async function handleImageSelected(file) {
    const previewUrl = URL.createObjectURL(file);
    replacePreview(previewUrl);
    dispatch({ type: "setImage", imageFile: null, previewUrl });
    setImage({ uploading: true, error: "" });

    try {
      const imageFile = await uploadCakeImage(file);
      dispatch({ type: "setImage", imageFile, previewUrl });
      setImage({ uploading: false, error: "" });
    } catch (error) {
      replacePreview(null);
      dispatch({ type: "clearImage" });
      const message = error.response?.status === 429
        ? "Has subido demasiadas fotos. Inténtalo de nuevo en un rato."
        : "No pudimos subir la foto. Revisa el archivo e inténtalo de nuevo.";
      setImage({ uploading: false, error: message });
    }
  }

  function handleImageCleared() {
    replacePreview(null);
    dispatch({ type: "clearImage" });
    setImage({ uploading: false, error: "" });
  }

  async function handleAddToCart() {
    setAdding(true);
    try {
      await addCustomCake(toCakeConfiguration(design), quantity);
      setAdded(true);
      toast.success("Tu torta está en el carrito");
    } catch (error) {
      const message = error.response?.data?.message;
      toast.error(message ? `No pudimos agregar tu torta: ${message}` : "No pudimos agregar tu torta al carrito");
    } finally {
      setAdding(false);
    }
  }

  function handleDesignAnother() {
    handleImageCleared();
    dispatch({ type: "reset" });
    setShowDietaryError(false);
    setQuantity(1);
    setAdded(false);
    setOpenField(FIRST_FIELD[0]);
  }

  if (status !== "ready") {
    return (
      <div className="personalization-page">
        <div className="personalization-state">
          {status === "loading" ? (
            <p>Preparando el obrador...</p>
          ) : (
            <>
              <p>No pudimos cargar las opciones de personalización.</p>
              <button type="button" className="cake-primary-button" onClick={retry}>
                Reintentar
              </button>
            </>
          )}
        </div>
      </div>
    );
  }

  const size = options.sizes.find((item) => item.id === design.sizeId);
  const flavour = options.flavours.find((item) => item.id === design.flavourId);
  const color = options.colors.find((item) => item.id === design.colorId);
  const price = estimateCakePrice(options, design);
  const phase = design.phase;
  const isSummary = phase === PHASES.length - 1;
  const cartPath = cart?.cartId || cartId ? `/cart/${cart?.cartId || cartId}` : "/cart";

  const cake = {
    tierDiameters: size.tierDiameters,
    decorativeTiers: design.decorativeTiers,
    frostingHex: color.hex,
    spongeHex: flavour.spongeColor,
    fillingHex: flavour.fillingColor,
    text: design.text,
    imageUrl: design.imagePreviewUrl,
    extraIds: design.extraIds,
  };
  const caption = [
    size.label,
    flavour.label,
    design.decorativeTiers ? formatDecorativeTiers(design.decorativeTiers) : null,
  ]
    .filter(Boolean)
    .join(" · ");

  return (
    <div className="personalization-page">
      <div className="personalization-top">
        <header className="personalization-header">
          <h1>Diseña tu torta</h1>
          <p>Elige cada detalle y mírala tomar forma. Nosotros la horneamos para ti.</p>
        </header>

        <PhaseStepper phases={PHASES} current={phase} onSelect={(index) => goToPhase(index)} />
      </div>

      <div className="personalization-layout">
        <div className="personalization-stage">
          <CakeViewer cake={cake} caption={caption} />
        </div>

        <section className="personalization-panel" aria-labelledby="personalization-phase-title">
          <div className="personalization-panel-header">
            <h2 id="personalization-phase-title" ref={headingRef} tabIndex={-1}>
              {PHASES[phase].label}
            </h2>
            {!isSummary && (
              <span className="personalization-estimate">
                Precio estimado <strong>${formatCOP(price)}</strong>
              </span>
            )}
          </div>

          {phase === 0 && (
            <BasePhase
              options={options}
              design={design}
              dispatch={dispatch}
              openField={openField}
              setOpenField={setOpenField}
              showDietaryError={showDietaryError}
              dietaryRef={dietaryRef}
            />
          )}

          {phase === 1 && (
            <DecorationPhase
              options={options}
              design={design}
              dispatch={dispatch}
              openField={openField}
              setOpenField={setOpenField}
              image={{ ...image, onSelect: handleImageSelected, onClear: handleImageCleared }}
            />
          )}

          {isSummary && !added && (
            <CakeSummary
              options={options}
              design={design}
              dispatch={dispatch}
              quantity={quantity}
              onQuantityChange={setQuantity}
              onEdit={(targetPhase, field) => goToPhase(targetPhase, field)}
              onAddToCart={handleAddToCart}
              adding={adding || image.uploading}
            />
          )}

          {isSummary && added && (
            <div className="cake-added" role="status">
              <span className="cake-added-seal" aria-hidden="true">
                <LuCheck />
              </span>
              <h3>Tu torta está en el carrito</h3>
              <p>Puedes seguir con el pago o diseñar otra para la misma celebración.</p>
              <div className="cake-added-actions">
                <button type="button" className="cake-primary-button" onClick={() => navigate(cartPath)}>
                  Ver carrito
                </button>
                <button type="button" className="cake-secondary-button" onClick={handleDesignAnother}>
                  Diseñar otra torta
                </button>
              </div>
            </div>
          )}

          {!isSummary && (
            <div className="personalization-nav">
              {phase > 0 ? (
                <button type="button" className="cake-secondary-button" onClick={() => goToPhase(phase - 1)}>
                  <LuArrowLeft aria-hidden="true" /> Atrás
                </button>
              ) : (
                <span />
              )}
              <button
                type="button"
                className="cake-primary-button"
                onClick={() => goToPhase(phase + 1)}
                disabled={image.uploading}
              >
                {phase === 0 ? "Continuar a decoración" : "Ver resumen"} <LuArrowRight aria-hidden="true" />
              </button>
            </div>
          )}
        </section>
      </div>

      <FaqSection id="cake-faq" title="Preguntas sobre tu torta" items={CAKE_FAQ} className="faq-cake" />
    </div>
  );
}
