import { lazy, Suspense } from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Layout from "../components/layout/Layout";
import HomePage from "../pages/HomePage";
import ProductsPage from "../pages/ProductsPage";
import ProductDetailPage from "../pages/ProductDetailPage";
import ShoppingCartPage from "../pages/ShoppingCartPage";
import ScrollToTop from "../components/layout/ScrollToTop";

// The 3D cake builder pulls in three.js: load it only when the route is visited
const PersonalizationPage = lazy(() => import("../pages/PersonalizationPage"));

// Pages a first-time visitor doesn't land on stay out of the entry bundle
const CheckoutPage = lazy(() => import("../pages/CheckoutPage"));
const OrderConfirmationPage = lazy(() => import("../pages/OrderConfirmationPage"));
const PaymentResultPage = lazy(() => import("../pages/PaymentResultPage"));
const AdminLoginPage = lazy(() => import("../pages/AdminLoginPage"));
const AdminMainPage = lazy(() => import("../pages/AdminMainPage"));
const PrivacyPolicyPage = lazy(() => import("../pages/PrivacyPolicyPage"));
const CookiePolicyPage = lazy(() => import("../pages/CookiePolicyPage"));

export default function AppRoutes() {
  return (
    <BrowserRouter>
      <ScrollToTop />
      <Layout>
        <Suspense fallback={<div className="route-loading">Cargando...</div>}>
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route
              path="/personalizar"
              element={
                <Suspense fallback={<div className="route-loading">Preparando tu torta...</div>}>
                  <PersonalizationPage />
                </Suspense>
              }
            />
            <Route path="/products" element={<ProductsPage />} />
            <Route path="/products/:id" element={<ProductDetailPage />} />
            <Route path="/cart" element={<ShoppingCartPage />} />
            <Route path="/cart/:id" element={<ShoppingCartPage />} />
            <Route path="/checkout/:id" element={<CheckoutPage />} />
            <Route path="/order/success/:orderId" element={<OrderConfirmationPage />} />
            <Route path="/payment/result/:reference" element={<PaymentResultPage />} />
            <Route path="/admin/login" element={<AdminLoginPage />} />
            <Route path="/admin" element={<AdminMainPage />} />
            <Route path="/politica-de-privacidad" element={<PrivacyPolicyPage />} />
            <Route path="/politica-de-cookies" element={<CookiePolicyPage />} />
          </Routes>
        </Suspense>
      </Layout>
    </BrowserRouter>
  );
}
