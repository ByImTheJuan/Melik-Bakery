import { createCart, getCart } from "./cartService";

const CART_ID_KEY = "cartId";


export function getCartId() {
  return localStorage.getItem(CART_ID_KEY);
}


export function setCartId(cartId) {
  localStorage.setItem(CART_ID_KEY, cartId);
}


export function clearCartId() {
  localStorage.removeItem(CART_ID_KEY);
}

export async function ensureCartId() {
  let cartId = getCartId();

  if (cartId) {
    const existingCart = await getCart(cartId);

    if (existingCart) {
      return cartId;
    }

    clearCartId();
  }

  const cart = await createCart();

  console.log("Created new cart:", cart);
  cartId = cart.cartId;

  setCartId(cartId);

  return cartId;
}
