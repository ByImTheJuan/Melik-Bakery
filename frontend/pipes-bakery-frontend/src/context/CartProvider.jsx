import { useEffect, useState, useMemo, useCallback } from "react";
import { CartContext } from "./cartContext";

import { getCart, addItem, removeItem, updateItemQuantity } from "../services/cartService";
import { clearCartId, ensureCartId, getCartId } from "../services/cartStorage";


export function CartProvider({ children }) {
  const [cartId, setCartId] = useState(null);
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);


  // Inicializar carrito existente, sin crear uno nuevo al entrar en la web.
  useEffect(() => {
    async function init() {
      const storedCartId = getCartId();

      if (!storedCartId) {
        setLoading(false);
        return;
      }

      try {
        const data = await getCart(storedCartId);

        if (data) {
          setCartId(storedCartId);
          setCart(data);
          return;
        }

        clearCartId();
      } catch (err) {
        if (import.meta.env.DEV) {
          console.error("Error initializing cart", err);
        }
      } finally {
        setLoading(false);
      }
    }

    init();
  }, []);


  // Recargar carrito
  const loadCart = useCallback(async () => {
    if (!cartId) return;

    const data = await getCart(cartId);
    setCart(data);
  }, [cartId]);


  // Anadir item
  const addToCart = useCallback(async (productId, quantity) => {
    const id = cartId || await ensureCartId();

    if (!cartId) {
      setCartId(id);
    }

    const data = await addItem(id, productId, quantity);
    setCart(data);
  }, [cartId]);

  // Eliminar item
  const removeFromCart = useCallback(async (productId) => {

    if (!cartId) return;

    await removeItem(cartId, productId);

    loadCart();

  }, [cartId, loadCart]);

  // Actualizar cantidad de item
  const updateQuantity = useCallback(async (productId, quantity) => {
    if (!cartId) return;

    const data = await updateItemQuantity(cartId, productId, quantity);
    setCart(data);
  }, [cartId]);

  const clearCart = useCallback(() => {
    clearCartId();
    setCartId(null);
    setCart(null);
  }, []);

  const value = useMemo(() => ({
    cartId,
    cart,
    loading,
    loadCart,
    addToCart,
    removeFromCart,
    updateQuantity,
    clearCart,
  }), [
    cartId,
    cart,
    loading,
    loadCart,
    addToCart,
    removeFromCart,
    updateQuantity,
    clearCart,
  ]);


  return (
    <CartContext.Provider value={value}>
      {children}
    </CartContext.Provider>
  );
}
