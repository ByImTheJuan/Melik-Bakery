import { useEffect, useState, useMemo, useCallback } from "react";
import { CartContext } from "./cartContext";

import { getCart, addItem, removeItem, updateItemQuantity } from "../services/cartService";
import { clearCartId, ensureCartId } from "../services/cartStorage";


export function CartProvider({ children }) {
  const [cartId, setCartId] = useState(null);
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);


  // Inicializar carrito
  useEffect(() => {
    async function init() {
      try {
        const id = await ensureCartId();
        const data = await getCart(id);

        setCartId(id);
        setCart(data);

      } catch (err) {
        console.error("Error initializing cart", err);
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


  // Añadir item
  const addToCart = useCallback(async (productId, quantity) => {
    if (!cartId) return;

    const data = await addItem(cartId, productId, quantity);
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

  const clearCart = useCallback(async () => {
    clearCartId();

    const newCartId = await ensureCartId();
    const newCart = await getCart(newCartId);

    setCartId(newCartId);
    setCart(newCart);

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
