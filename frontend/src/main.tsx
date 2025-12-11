import "./styles/global.css";
import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import App from "./App.tsx";
import { BrowserRouter } from "react-router-dom";
import { ThemeProvider } from "./components/ThemeProvider.tsx";
import { PageProvider } from "./components/PageProvider.tsx";

createRoot(document.getElementById("root")!).render(
  <ThemeProvider>
    <PageProvider>
      <BrowserRouter>
        <StrictMode>
          <App />
        </StrictMode>
      </BrowserRouter>
    </PageProvider>
  </ThemeProvider>
);
