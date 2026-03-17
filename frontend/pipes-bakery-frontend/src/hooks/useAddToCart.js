import { useCart } from "../context/CartContext";


export function useAddToCart() {

  const { addToCart } = useCart();


  async function handleAddToCart(productId, quantity = 1) {

    try {

      await addToCart(productId, quantity);

    } catch (err) {

      console.error("Error adding to cart", err);

    }

  }


  return {
    addToCart: handleAddToCart,
  };
}