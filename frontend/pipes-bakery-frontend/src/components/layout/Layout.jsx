import { useLocation } from "react-router-dom";
import Footer from "./Footer";
import Navbar from "./Navbar";


export default function Layout({ children }) {
  const location = useLocation();
  const isAdminRoute = location.pathname.startsWith("/admin");

  return (
    <div className="layout">
      {!isAdminRoute && <Navbar />}
      <main>{children}</main>
      <Footer />
    </div>
  );
}
