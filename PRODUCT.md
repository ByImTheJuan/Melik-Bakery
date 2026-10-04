# Product

<!-- impeccable:product-schema 1 -->

## Platform

web

## Users

- **Celebration buyers in Bogotá**: people ordering cakes and desserts for birthdays, events and gatherings, where the dessert is meant to be the centre of the occasion.
- **Gift senders**: buyers who order a dessert to be delivered to someone else's address as a present (checkout captures a separate receiver name and the recipient's shipping address).
- **The bakery owner (admin)**: Felipe manages the catalog (including display order and product images) and moves paid orders through preparation, shipping and delivery.

Visitors arrive on both mobile and desktop in roughly equal measure; neither is secondary.

## Product Purpose

Melik Bakery is the online shop of a one-person artisan bakery in Bogotá. Customers browse a catalog of "postres de autor" or customize a cake of their own, add items to a cart, pay online and have them delivered within Bogotá, with no account needed. Success is a customer confidently ordering a dessert for a celebration or gift, paying, and knowing exactly what was ordered and that it was received.

## Positioning

An authored, personal bakery rather than a generic shop: every product is made by Felipe Hernández Derch, whose craft grew from baking at home with his father, through formal courses, to an internship at a classic Boulangerie et Pâtisserie in Brussels. The name comes from Catalan *Melic* (navel; roots, the centre of something), a tribute to his Catalan roots. The stated philosophy is authenticity: honest processes and carefully selected, high-quality ingredients, with the aim of being "el centro de tus celebraciones".

## Operating Context

- Spanish-language storefront for customers in Bogotá, Colombia; prices in Colombian pesos (COP).
- Guest checkout only: contact details, an optional receiver name, and a Bogotá shipping address (city and country fixed to Bogotá, Colombia).
- Payment through Wompi; an order exists only once the payment is approved. Failed payments can be retried or the customer returns to the cart.
- After payment, the customer sees a confirmation with the order number, their name and email, and the ordered items and totals.
- Admin works in a separate authenticated panel.

## Capabilities and Constraints

- Delivery only within Bogotá, at a flat shipping cost.
- **Products are made to order**: baking happens after the order is placed, so there is a lead time before delivery. The exact lead time is not yet defined and must not be invented.
- **Capacity is limited**: one person bakes, so daily volume is limited and some days may be unavailable. How this is communicated or enforced on the site is not yet decided.
- No customer accounts, no order tracking for customers, no transactional emails yet. Do not promise email notifications the system does not send.
- Product ingredients are a free-text list, not a structured allergen system; do not present allergen guarantees.
- Product images are served by the backend and uploaded by the admin.

## Brand Commitments

- Name: **Melik Bakery**. Tagline in use: "Postres de autor horneados con amor."
- Voice: warm, personal and first-person (Felipe speaks as the baker), in Spanish; closing line "Bienvenidos al origen. Bienvenidos a Melik."
- Existing logo (`frontend/pipes-bakery-frontend/public/images/logo.webp`) and favicon.

## Evidence on Hand

- Founder story and philosophy copy on the home page (`src/components/home/AboutSection.jsx`).
- Real product photography for the home carousel and about section (`public/images/`), and product images under `data/products-images/`.
- Real contact details: +57 319 383 0446, melik.bakery@hyd.net.co, Instagram @melik.bakery.
- No customer testimonials, reviews, press, order counts or ratings exist; do not fabricate them.

## Product Principles

1. **The maker is the brand**: keep Felipe's authorship and story visible; this is a person's craft, not a catalog warehouse.
2. **Every order is an occasion**: support celebrations and gifting (receiver, delivery address, clear confirmation) as first-class, not edge cases.
3. **Never overpromise a one-person kitchen**: be honest about made-to-order timing and limited capacity rather than implying instant, unlimited availability.
4. **Certainty at checkout**: customers must always know what they are paying, what happened to their payment, and what they ordered.
