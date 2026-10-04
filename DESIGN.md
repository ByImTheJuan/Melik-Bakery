---
name: Melik Bakery
description: Postres de autor horneados con amor. Crea tu torta perfecta para cumpleaños, bodas, comuniones y cualquier ocasión especial. Warm, personal storefront for a one-person artisan bakery in Bogotá.
colors:
  roasted-cacao: "#5a2d0c"
  caramel-glaze: "#c68642"
  toffee: "#a87b51"
  copper-crust: "#b97a56"
  copper-crust-deep: "#8c5a3d"
  flour-cream: "#f8f4ef"
  parchment: "#f5e6d3"
  biscuit: "#e9c99f"
  milk-foam: "#f1e3d3"
  warm-white: "#fffdfa"
  pure-white: "#ffffff"
  oat-border: "#d8c8bc"
  locked-field: "#f3ebe4"
  error-red: "#b00020"
  success-green: "#166534"
typography:
  display:
    fontFamily: "Tahoma, 'Segoe UI', Geneva, Verdana, sans-serif"
    fontSize: "3.5rem"
    fontWeight: 700
    lineHeight: 1.1
  headline:
    fontFamily: "Tahoma, 'Segoe UI', Geneva, Verdana, sans-serif"
    fontSize: "clamp(2rem, 4vw, 3rem)"
    fontWeight: 700
    lineHeight: 1.1
  title:
    fontFamily: "Tahoma, 'Segoe UI', Geneva, Verdana, sans-serif"
    fontSize: "1.2rem"
    fontWeight: 600
    lineHeight: 1.3
  body:
    fontFamily: "Tahoma, 'Segoe UI', Geneva, Verdana, sans-serif"
    fontSize: "1.05rem"
    fontWeight: 400
    lineHeight: 1.7
  label:
    fontFamily: "Tahoma, 'Segoe UI', Geneva, Verdana, sans-serif"
    fontSize: "0.85rem"
    fontWeight: 400
    letterSpacing: "0.06em"
rounded:
  input: "8px"
  button: "14px"
  tile: "16px"
  panel: "20px"
  card-hero: "24px"
  pill: "999px"
spacing:
  xs: "0.5rem"
  sm: "1rem"
  md: "1.5rem"
  lg: "2rem"
  xl: "3rem"
components:
  button-primary:
    backgroundColor: "{colors.toffee}"
    textColor: "{colors.pure-white}"
    rounded: "{rounded.button}"
    padding: "0.95rem 1.4rem"
  button-secondary:
    backgroundColor: "{colors.pure-white}"
    textColor: "{colors.roasted-cacao}"
    rounded: "{rounded.button}"
    padding: "0.95rem 1.4rem"
  button-checkout:
    backgroundColor: "{colors.copper-crust}"
    textColor: "{colors.pure-white}"
    rounded: "12px"
    padding: "1rem"
  input-field:
    backgroundColor: "{colors.warm-white}"
    textColor: "{colors.roasted-cacao}"
    rounded: "{rounded.input}"
    padding: "0.8rem"
  input-field-locked:
    backgroundColor: "{colors.locked-field}"
    textColor: "#6b5446"
    rounded: "{rounded.input}"
    padding: "0.8rem"
  badge:
    backgroundColor: "#f4e5d5"
    textColor: "{colors.roasted-cacao}"
    rounded: "{rounded.pill}"
    padding: "0.45rem 0.85rem"
  card-hero:
    backgroundColor: "{colors.flour-cream}"
    rounded: "{rounded.card-hero}"
    padding: "3rem"
  detail-tile:
    backgroundColor: "#ffffffb8"
    rounded: "{rounded.tile}"
    padding: "1rem 1.1rem"
---

# Design System: Melik Bakery

## Overview

**Creative North Star: "The Baker's Table"**

The site should feel like being served across Felipe's own table: warm wood-and-cacao browns, flour-dusted creams, and nothing between the customer and the maker. Every surface is a shade of something you'd find in the kitchen (cacao, caramel, toffee, parchment, biscuit), so the whole storefront reads as one warm material rather than a white page with brown accents.

It is soft and inviting rather than formal: generously rounded containers, gently lifted cards with brown-tinted shadows, pill-shaped badges, and buttons that rise a couple of pixels on hover. Density is comfortable, with roomy padding and short line lengths, because people ordering for a celebration or a gift should never feel rushed or crowded.

It must never feel cold or corporate (no greys, blues or stark white SaaS chrome), never childish or cartoonish (no candy colours or bubbly illustration), and never like a generic bakery template. Personality comes from the maker's voice, real product photography and the warm palette, not from decoration.

**Key Characteristics:**
- One warm brown family carries text, accents and surfaces; white appears only as a lifted card or button face.
- Soft, generously rounded forms (8px fields up to 24px hero cards; pills for badges).
- Brown-tinted ambient shadows, never neutral grey.
- A single humanist sans (Tahoma) for everything; hierarchy comes from size and weight.
- Gentle motion: 0.2–0.3s ease transitions, 2px hover lifts, underline-grow nav links.

## Colors

A single warm, earthy brown-and-cream family; accents are deeper or more saturated versions of the same kitchen materials.

### Primary
- **Roasted Cacao** (#5a2d0c): the brand's voice. Body text, headings, nav links and icon strokes; also the colour of the logo world. Text is never black.

### Secondary
- **Caramel Glaze** (#c68642): the warm accent for highlights and tinted badge backgrounds (mixed ~18% into white).
- **Toffee** (#a87b51): primary action buttons on confirmation/result cards, nav underline, active nav state, and tinted borders (mixed 18–30% into white).
- **Copper Crust** (#b97a56 → **Copper Crust Deep** #8c5a3d): the checkout gradient (135deg) for the single most important action, "Confirmar pedido"; also the input focus ring colour.

### Neutral
- **Flour Cream** (#f8f4ef): base page background and top of the navbar gradient.
- **Parchment** (#f5e6d3): second surface tone; bottom of the navbar gradient and card gradient mid-stop.
- **Biscuit** (#e9c99f): third tone, used diluted in card gradients.
- **Milk Foam** (#f1e3d3): soft accent surface.
- **Warm White** (#fffdfa): form field fill.
- **Oat Border** (#d8c8bc): field strokes.
- **Locked Field** (#f3ebe4): fixed, read-only fields (e.g. City/Country at checkout), with muted text #6b5446.
- **Error Red** (#b00020) and **Success Green** (#166534): status text only, never surfaces.

### Named Rules
**The Kitchen Palette Rule.** Every colour must be nameable as a baking material. If a new colour can't be (a grey, a blue, a neon), it doesn't belong.

**The No-Black Rule.** Text is Roasted Cacao (#5a2d0c), not #000 or neutral grey.

## Typography

**Display Font:** Tahoma (with 'Segoe UI', Geneva, Verdana, sans-serif)
**Body Font:** Tahoma (same stack)

**Character:** One humble, legible humanist sans for everything. It keeps the voice plain and personal; warmth comes from colour and copy, not typographic flourish.

### Hierarchy
- **Display** (700, 3.5rem, 1.1): section statements such as the about heading "El artista detrás de Melik"; drops to ~2rem on mobile.
- **Headline** (700, clamp(2rem, 4vw, 3rem), 1.1): page-level card titles (order confirmed, payment result).
- **Title** (600, 1.2rem, 1.3): product names, section titles inside cards.
- **Body** (400, 1.05rem, 1.7): paragraphs; kept to about 52ch inside cards.
- **Label** (400, 0.85rem, 0.06em tracking, uppercase): small field captions in detail tiles ("Numero de pedido", "Cliente").

### Named Rules
**The One Voice Rule.** A single typeface. Don't introduce a display serif or script to "add personality"; personality is the maker's words.

## Layout

Content sits in centred columns with comfortable side padding: about 6% horizontal padding on the navbar, focused cards capped at 700–900px (payment result 700px, order confirmation 900px), 1.5rem side gutters that drop to 1rem on phones. Pages use generous vertical rhythm (3rem top margin, 4rem bottom). Detail tiles flow in an auto-fit grid (minmax(180px, 1fr), 1rem gap). The main breakpoint is 768px: card padding drops from 3rem to 2rem/1.5rem, action rows stack vertically, and the navbar collapses to a hamburger. Mobile and desktop are equally important.

## Elevation & Depth

Layered and gently lifted: surfaces are tonal (cream on parchment gradients), and key cards float on soft, wide, brown-tinted ambient shadows. Shadows are atmospheric, not structural, and grow slightly on hover.

### Shadow Vocabulary
- **Card float** (`box-shadow: 0 20px 45px rgba(90, 45, 12, 0.12)`): hero/result/confirmation cards.
- **Button lift** (`box-shadow: 0 14px 28px rgba(90, 45, 12, 0.18)`): primary buttons.
- **Navbar scrolled** (`box-shadow: 0 4px 20px rgba(0,0,0,0.08)` + `backdrop-filter: blur(8px)`): sticky navbar once the page scrolls.
- **Focus glow** (`box-shadow: 0 0 0 4px rgba(185, 122, 86, 0.14)`): focused form fields.

### Named Rules
**The Warm Shadow Rule.** New shadows are tinted with Roasted Cacao (rgba(90, 45, 12, …)), not neutral black.

## Shapes

Soft, generous rounding throughout: 8px for fields, 12–14px for buttons, 16px for inner tiles, 20–24px for hero and summary cards, and full pills (999px) for badges and the hamburger bars. Borders are thin (1px) and tinted from Toffee, never grey. Images are softly clipped to match their container.

## Components

### Buttons
Soft, warm and tactile.
- **Shape:** gently rounded (14px; checkout uses 12px).
- **Primary:** Toffee fill with white text, 700 weight, 0.95rem × 1.4rem padding, button-lift shadow.
- **Checkout:** full-width Copper Crust gradient (135deg, #b97a56 → #8c5a3d), white 700 text; the single strongest call to action on the site.
- **Secondary:** white face, Roasted Cacao text, 1px Toffee-tinted border.
- **Hover / Focus:** rise 2px (`translateY(-2px)`) with `brightness(1.02)`, 0.2s ease. Disabled: 70% opacity, no lift, not-allowed cursor.

### Badges
- **Style:** pill (999px), Caramel tint (~18% into white), Roasted Cacao 700 text at 0.9rem. They sit above card headlines to name the state ("Pedido confirmado", "Procesando pago"). The failure badge swaps to a red-tinted fill (#b00020 at 12%) with #8a0019 text.

### Cards / Containers
- **Corner Style:** 24px on hero/result cards, 16px on inner tiles.
- **Background:** diagonal cream gradient (Flour Cream → Parchment → diluted Biscuit, 145deg); inner tiles are translucent white (72%).
- **Shadow Strategy:** card float (see Elevation).
- **Border:** 1px Toffee tinted to 18–28%.
- **Internal Padding:** 3rem desktop, 2rem × 1.5rem mobile; tiles 1rem × 1.1rem.

### Inputs / Fields
- **Style:** Warm White fill, 1px Oat Border, 8px radius, 0.8rem padding, stacked full-width.
- **Focus:** border shifts to Copper Crust with a soft 4px copper glow; fill becomes pure white.
- **Locked (read-only):** Locked Field fill, muted brown text, not-allowed cursor, no focus glow; used for values the customer can't change.

### Navigation
- **Style:** sticky 5rem bar with a vertical Parchment-to-Flour-Cream gradient; once scrolled it turns solid #e8d4bc with blur and a soft shadow. Links are Roasted Cacao, 1.1rem, 700.
- **Hover / Active:** a 2px Toffee underline grows from the left (0.3s); the active link turns Toffee.
- **Mobile:** collapses to a three-bar hamburger (rounded bars) that morphs into an X.

### Order Detail Card (signature)
The confirmation pattern: a Toffee check-mark seal (52px circle, white 2.25 stroke, lift shadow; 44px on mobile) beside the headline → full-width reassurance paragraph → auto-fit grid of label/value tiles with the order number on its own full row in large tracked tabular digits, then customer and email → itemised summary panel with tabular amounts (product, quantity × unit price, line total, then subtotal, shipping and a bold "Total pagado") → two actions (primary + secondary).

### Cake Builder (signature)
The personalization page ("Diseña tu torta", `/personalizar`) is the site's main attraction and the hero's primary action. Title block and a three-step stepper share the top row; below, a two-column stage: the live 3D cake on the left (sticky, 24px card on a cream radial glow, a pill caption with size · tiers · flavour) and the options panel on the right (cream gradient card).
- **Stepper:** numbered 40px circles joined by a 2px Toffee-tint line. Current step is a solid Toffee circle with a soft halo; completed steps turn into a check and stay clickable; the line fills Toffee as the customer advances.
- **Option fields:** stacked accordion rows (translucent white, 16px) showing the field name and current choice; only one is open at a time. Options are white 14px cards with a 1px Toffee-tint border; the selected one gets a Toffee border, a faint caramel wash and a 3px Toffee halo. Cards carry small drawn glyphs (diameter to scale, sponge cross-section, stacked tiers) instead of icons.
- **Cake glyphs:** size and tier cards carry a tiny side view of the cake drawn to scale (one bar per tier, cream with a Toffee edge); decorative (fake) tiers are drawn dashed, with a legend, so it's clear they only add height.
- **Dietary question:** a compact always-visible row above the size ("¿Alguna restricción alimentaria?" with No/Sí pills, Toffee when selected); "Sí" reveals a text field. Continuing without an answer outlines the row in Error Red with a plain-language message. In the admin order view a restriction is shown first, on a red-tinted strip.
- **Colours on the cake** stay soft and kitchen-made (cream, vanilla, strawberry pink, caramel, chocolate, pistachio, lavender) even though the product itself is colourful.
- **3D scene:** warm studio light (no remote HDR), brown-tinted contact shadow, glazed ceramic stand, and a served slice on a plate that shows the chosen sponge and filling. Changes ease in (no snapping); the cake slowly turns until the customer drags it, and stops for reduced motion.

## Do's and Don'ts

### Do:
- **Do** set text in Roasted Cacao (#5a2d0c) and build surfaces from Flour Cream, Parchment and Biscuit.
- **Do** keep forms soft: 8px fields, 14px buttons, 16–24px containers, pill badges.
- **Do** tint shadows and borders with the brand browns (rgba(90, 45, 12, …), Toffee mixes).
- **Do** keep a single primary action per view and use the Copper Crust gradient only for checkout.
- **Do** show locked values (fixed City/Country) as read-only Locked Field inputs, not hidden or plain text.

### Don't:
- **Don't** use cold or corporate styling: greys, blues, stark white SaaS chrome or neutral-black shadows.
- **Don't** go childish or cartoonish: no candy colours, bubbly illustrations or novelty fonts.
- **Don't** make it look like a generic bakery template; let the maker's story, real photos and voice carry identity.
- **Don't** introduce a second typeface or decorative script.
- **Don't** use colour outside the kitchen palette except Error Red and Success Green for status text.
