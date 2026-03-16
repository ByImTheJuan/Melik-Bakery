import { useEffect, useState } from "react";
import { getAllProducts } from "../services/productService";
import { handleAxiosError } from "../utils/handleAxiosError";

export const useProducts = () => {
  const [products, setProducts] = useState([]);
  const [status, setStatus] = useState("idle");
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    const fetchProducts = async () => {
      setStatus("loading");

      try {
        const data = await getAllProducts();
        setProducts(data);
        setStatus("success");
      } catch (error) {
        const message = handleAxiosError(error);
        setErrorMessage(message);
        setStatus("error");
      }
    };

    fetchProducts();
  }, []);

  return {
    products,
    status,
    errorMessage,
  };
};