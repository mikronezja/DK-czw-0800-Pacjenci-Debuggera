import { Route, Routes } from "react-router-dom";
import AdminPanel from "./components/pages/admin-panel/AdminPanel";
import Home from "./components/pages/Home";
import Navbar from "./components/navbar/Navbar";
import DetailsPage from "./components/pages/details/DetailsPage";
import { Switch } from "./components/ui/switch";
import { useState } from "react";

function App() {
  const [checked, setChecked] = useState(false);
  return (
    <>
      <Navbar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/admin-panel" element={<AdminPanel />} />
        <Route path="/details/:idValue" element={<DetailsPage />} />
      </Routes>
    </>
  );
}

export default App;
