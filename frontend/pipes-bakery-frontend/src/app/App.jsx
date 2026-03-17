import AppRoutes from "./routes";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

function App() {
  return (
    <>
      
      <AppRoutes />

      <ToastContainer 
        position="bottom-right"
        autoClose={1500}
        hideProgressBar
        newestOnTop
        closeOnClick
        pauseOnHover
        theme="light"/>
    </>
  );
}

export default App;
