import { StrictMode } from 'react'
import ReactDOM from 'react-dom/client'
import App from './app/App.jsx'
import "./styles/layout.css";

ReactDOM.createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>
);
