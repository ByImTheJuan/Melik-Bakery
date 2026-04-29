import { useEffect, useMemo, useState } from "react";
import {
  createProduct,
  deleteProduct,
  getAllProducts,
  updateProduct,
} from "../../services/productService";
import { formatCOP } from "../../utils/formatPrice";
import "../../styles/productsAdmin.css";

const emptyForm = {
  name: "",
  description: "",
  price: "",
  ingredients: [""],
  imageUrl: "",
};

function mapProductToForm(product) {
  return {
    name: product.name,
    description: product.description ?? "",
    price: product.price?.toString() ?? "",
    ingredients: product.ingredients.length > 0 ? [...product.ingredients] : [""],
    imageUrl: product.imageUrl,
  };
}

function buildPayload(formData) {
  return {
    name: formData.name.trim(),
    description: formData.description.trim(),
    price: Number(formData.price),
    ingredients: formData.ingredients
      .map((ingredient) => ingredient.trim())
      .filter(Boolean),
    imageUrl: formData.imageUrl.trim(),
  };
}

export default function ProductsAdmin() {
  const [products, setProducts] = useState([]);
  const [formData, setFormData] = useState(emptyForm);
  const [editingProductId, setEditingProductId] = useState(null);
  const [status, setStatus] = useState("loading");
  const [errorMessage, setErrorMessage] = useState("");
  const [submitMessage, setSubmitMessage] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isDeletingId, setIsDeletingId] = useState(null);

  const submitLabel = useMemo(
    () => (editingProductId ? "Guardar cambios" : "Crear producto"),
    [editingProductId]
  );

  useEffect(() => {
    loadProducts();
  }, []);

  async function loadProducts() {
    setStatus("loading");
    setErrorMessage("");

    try {
      const data = await getAllProducts();
      setProducts(data);
      setStatus("success");
    } catch (error) {
      setErrorMessage(
        error.response?.data?.message ??
          "No se pudieron cargar los productos."
      );
      setStatus("error");
    }
  }

  function handleChange({ target }) {
    const { name, value } = target;
    setFormData((current) => ({
      ...current,
      [name]: value,
    }));
  }

  function handleIngredientChange(index, value) {
    setFormData((current) => ({
      ...current,
      ingredients: current.ingredients.map((ingredient, ingredientIndex) =>
        ingredientIndex === index ? value : ingredient
      ),
    }));
  }

  function handleAddIngredient() {
    setFormData((current) => ({
      ...current,
      ingredients: [...current.ingredients, ""],
    }));
  }

  function handleRemoveIngredient(index) {
    setFormData((current) => {
      const nextIngredients = current.ingredients.filter((_, ingredientIndex) => ingredientIndex !== index);

      return {
        ...current,
        ingredients: nextIngredients.length > 0 ? nextIngredients : [""],
      };
    });
  }

  function handleEdit(product) {
    setEditingProductId(product.id);
    setFormData(mapProductToForm(product));
    setSubmitMessage("");
    setErrorMessage("");
  }

  function handleCancelEdit() {
    setEditingProductId(null);
    setFormData(emptyForm);
    setSubmitMessage("");
    setErrorMessage("");
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setIsSubmitting(true);
    setSubmitMessage("");
    setErrorMessage("");

    try {
      const payload = buildPayload(formData);

      if (editingProductId) {
        await updateProduct(editingProductId, payload);
        handleCancelEdit();
        setSubmitMessage("Producto actualizado correctamente.");
      } else {
        await createProduct(payload);
        setFormData(emptyForm);
        setSubmitMessage("Producto creado correctamente.");
      }

      await loadProducts();
    } catch (error) {
      const apiErrors = error.response?.data?.errors;
      const apiMessage = error.response?.data?.message;

      setErrorMessage(
        apiErrors?.[0] ??
          apiMessage ??
          "No se pudo guardar el producto."
      );
    } finally {
      setIsSubmitting(false);
    }
  }

  async function handleDelete(productId) {
    setIsDeletingId(productId);
    setSubmitMessage("");
    setErrorMessage("");

    try {
      await deleteProduct(productId);

      if (editingProductId === productId) {
        handleCancelEdit();
      }

      setProducts((current) => current.filter((product) => product.id !== productId));
      setSubmitMessage("Producto eliminado correctamente.");
    } catch (error) {
      setErrorMessage(
        error.response?.data?.message ??
          "No se pudo eliminar el producto."
      );
    } finally {
      setIsDeletingId(null);
    }
  }

  return (
    <section className="products-admin">
      <div className="products-admin-header">
        <div>
          <span className="products-admin-eyebrow">Gestión de productos</span>
          <h2>Catálogo de panadería</h2>
          <p>
            Crea productos nuevos, actualiza la información existente o elimina referencias del catálogo.
          </p>
        </div>

        <button className="products-admin-secondary" onClick={handleCancelEdit}>
          Nuevo producto
        </button>
      </div>

      {(errorMessage || submitMessage) && (
        <div className={errorMessage ? "products-admin-alert error" : "products-admin-alert success"}>
          {errorMessage || submitMessage}
        </div>
      )}

      <div className="products-admin-layout">
        <div className="products-admin-panel">
          <div className="products-admin-list-header">
            <h3>Productos existentes</h3>
            <span>{products.length} elementos</span>
          </div>

          {status === "loading" && <p className="products-admin-empty">Cargando productos...</p>}
          {status === "error" && <p className="products-admin-empty">{errorMessage}</p>}

          {status === "success" && products.length === 0 && (
            <p className="products-admin-empty">Todavía no hay productos en la base de datos.</p>
          )}

          {status === "success" && products.length > 0 && (
            <div className="products-admin-list">
              {products.map((product) => (
                <article className="products-admin-item" key={product.id}>
                  <div className="products-admin-item-image">
                    <img
                      src={`${import.meta.env.VITE_IMAGES_BASE_URL}${product.imageUrl}`}
                      alt={product.name}
                    />
                  </div>

                  <div className="products-admin-item-content">
                    <div className="products-admin-item-top">
                      <div>
                        <h4>{product.name}</h4>
                        <p className="products-admin-item-description">
                          {product.description || "Sin descripción"}
                        </p>
                      </div>
                      <strong>${formatCOP(product.price)}</strong>
                    </div>

                    <p className="products-admin-ingredients">
                      {product.ingredients.join(", ")}
                    </p>

                    <div className="products-admin-item-actions">
                      <button
                        className="products-admin-secondary"
                        onClick={() => handleEdit(product)}
                      >
                        Editar
                      </button>

                      <button
                        className="products-admin-danger"
                        onClick={() => handleDelete(product.id)}
                        disabled={isDeletingId === product.id}
                      >
                        {isDeletingId === product.id ? "Eliminando..." : "Eliminar"}
                      </button>
                    </div>
                  </div>
                </article>
              ))}
            </div>
          )}
        </div>

        <div className="products-admin-panel products-admin-form-panel">
          <div className="products-admin-list-header">
            <h3>{editingProductId ? "Editar producto" : "Crear producto"}</h3>
            {editingProductId && <span>ID {editingProductId}</span>}
          </div>

          <form className="products-admin-form" onSubmit={handleSubmit}>
            <label>
              <span>Nombre</span>
              <input
                name="name"
                value={formData.name}
                onChange={handleChange}
                required
              />
            </label>

            <label>
              <span>Descripción</span>
              <textarea
                name="description"
                value={formData.description}
                onChange={handleChange}
                rows="4"
              />
            </label>

            <label>
              <span>Precio</span>
              <input
                type="number"
                min="0"
                step="0.01"
                name="price"
                value={formData.price}
                onChange={handleChange}
                required
              />
            </label>

            <div className="products-admin-ingredients-group">
              <div className="products-admin-ingredients-header">
                <span>Ingredientes</span>
                <button
                  type="button"
                  className="products-admin-secondary products-admin-ingredient-add"
                  onClick={handleAddIngredient}
                >
                  Añadir ingrediente
                </button>
              </div>

              <div className="products-admin-ingredient-list">
                {formData.ingredients.map((ingredient, index) => (
                  <div className="products-admin-ingredient-row" key={`${editingProductId ?? "new"}-${index}`}>
                    <input
                      value={ingredient}
                      onChange={(event) => handleIngredientChange(index, event.target.value)}
                      required
                    />

                    <button
                      type="button"
                      className="products-admin-danger products-admin-ingredient-remove"
                      onClick={() => handleRemoveIngredient(index)}
                      disabled={formData.ingredients.length === 1 && !ingredient.trim()}
                    >
                      Eliminar
                    </button>
                  </div>
                ))}
              </div>
            </div>

            <label>
              <span>Ruta de imagen</span>
              <input
                name="imageUrl"
                value={formData.imageUrl}
                onChange={handleChange}
                placeholder="/images/products/nombre-del-archivo.jpg"
                required
              />
            </label>

            <div className="products-admin-form-actions">
              {editingProductId && (
                <button
                  type="button"
                  className="products-admin-secondary"
                  onClick={handleCancelEdit}
                >
                  Cancelar
                </button>
              )}

              <button
                type="submit"
                className="products-admin-primary"
                disabled={isSubmitting}
              >
                {isSubmitting ? "Guardando..." : submitLabel}
              </button>
            </div>
          </form>
        </div>
      </div>
    </section>
  );
}
