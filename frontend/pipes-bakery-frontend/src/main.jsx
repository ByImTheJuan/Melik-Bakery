import { StrictMode } from 'react'
import ReactDOM from 'react-dom/client'
import App from './app/App.jsx'
import "./styles/layout.css";
import { CartProvider } from './context/CartProvider.jsx';

ReactDOM.createRoot(document.getElementById('root')).render(
  <StrictMode>
    <CartProvider>
      <App />
    </CartProvider>
  </StrictMode>
);
