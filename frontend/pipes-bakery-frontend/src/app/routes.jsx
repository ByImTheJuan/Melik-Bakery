import { BrowserRouter, Routes, Route } from "react-router-dom";
import HomePage from "../pages/HomePage";
import ProductsPage from "../pages/ProductsPage";
import ProductDetailPage from "../pages/ProductDetailPage";
import Layout from "../components/layout/Layout";

export default function AppRoutes() {
  return (
    <BrowserRouter>
      <Layout>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/products" element={<ProductsPage />} />
          <Route path="/products/:id" element={<ProductDetailPage />} />
        </Routes>
      </Layout>
    </BrowserRouter>
  );
}