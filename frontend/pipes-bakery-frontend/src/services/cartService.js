import apiClient from "../api/apiClient";


export async function createCart() {
  if (import.meta.env.DEV) {
    console.log("Creating new cart...");
  }
  const response = await apiClient.post("/cart");
  return await response.data;
}

export async function getCart(cartId) {
  if (!cartId) {
    return null;
  }

  try {
    const response = await apiClient.get(`/cart/${cartId}`);
    return response.data;
  } catch (error) {
    if (import.meta.env.DEV) {
      console.error("Error fetching cart:", error);
      if (error.response?.status === 404) {
        console.log("Cart not found, returning null");
        return null;
      }
      throw error;
    }
  }
}

export async function addItem(cartId, productId, quantity) {
  if (import.meta.env.DEV) {
    console.log("Adding item to cart...", { cartId, productId, quantity });
  }
  const response = await apiClient.post(`/cart/${cartId}/items`, {
    productId,
    quantity
  });
  return await response.data;
}

export async function removeItem(cartId, productId) {
  if (import.meta.env.DEV) {
    console.log("Removing item from cart...", { cartId, productId });
  }
  const response = await apiClient.delete(`/cart/${cartId}/items/${productId}`);
  return await response.data;
}

export async function updateItemQuantity(cartId, productId, quantity) {
  const response = await apiClient.put(`/cart/${cartId}/items/${productId}`, {
    productId,
    quantity
  });
  return await response.data;
}

export async function clearCart(cartId) {
  const response = await apiClient.delete(`/cart/${cartId}`);
  return await response.data;
}

export async function checkoutCart(cartId, checkoutData) {
  const response = await apiClient.post(`/cart/${cartId}/checkout`, checkoutData);
  return await response.data;
}
