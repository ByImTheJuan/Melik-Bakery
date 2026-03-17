export const handleAddToCart = async () => {

    if (!product) return;

    try {

      await addToCart(product.id, quantity);

      console.log("Added to cart");

    } catch (err) {

      console.error("Error adding to cart", err);

    }

  };