import { useCart } from "../hooks/useCart";
import { toast } from "react-toastify";


export function useAddToCart() {

  const { addToCart } = useCart();


  async function handleAddToCart(productId, quantity = 1) {

    try {

      await addToCart(productId, quantity);
      toast.success("Producto añadido al carrito");

    } catch (err) {

      toast.error("Error al añadir producto al carrito");
      
      if (import.meta.env.DEV) {
        console.error("Error adding to cart:", err);
      }
    }

  }


  return {
    addToCart: handleAddToCart,
  };
}