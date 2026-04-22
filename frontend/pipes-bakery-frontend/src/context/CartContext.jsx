import { createContext, useContext, useEffect, useState } from "react";

import { getCart, addItem, removeItem,  updateItemQuantity } from "../services/cartService";
import { ensureCartId } from "../services/cartStorage";



const CartContext = createContext();


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
  async function loadCart() {
    if (!cartId) return;

    const data = await getCart(cartId);
    setCart(data);
  }


  // Añadir item
  async function addToCart(productId, quantity) {
    if (!cartId) return;

    const data = await addItem(cartId, productId, quantity);
    setCart(data);
  }

  // Eliminar item
  async function removeFromCart(productId) {

    if (!cartId) return;

    await removeItem(cartId, productId);

    loadCart();
  }

  // Actualizar cantidad de item
  async function updateQuantity(productId, quantity) {
    if (!cartId) return;

    const data = await updateItemQuantity(cartId, productId, quantity);
    setCart(data);
  }

  const value = {
    cartId,
    cart,
    loading,
    loadCart,
    addToCart,
    removeFromCart,
    updateQuantity,
  };


  return (
    <CartContext.Provider value={value}>
      {children}
    </CartContext.Provider>
  );
}


export function useCart() {
  return useContext(CartContext);
}