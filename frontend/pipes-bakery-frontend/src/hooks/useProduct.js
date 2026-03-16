import { useEffect, useState } from "react";
import { getProductById } from "../services/productService";
import { handleAxiosError } from "../utils/handleAxiosError";

export const useProduct = (id) => {
  const [product, setProduct] = useState(null);
  const [status, setStatus] = useState("idle");
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    if (!id) return;

    const fetchProduct = async () => {
      setStatus("loading");

      try {
        const data = await getProductById(id);
        setProduct(data);
        setStatus("success");
      } catch (error) {
        const message = handleAxiosError(error);
        setErrorMessage(message);
        setStatus("error");
      }
    };

    fetchProduct();
  }, [id]);

  return {
    product,
    status,
    errorMessage,
  };
};