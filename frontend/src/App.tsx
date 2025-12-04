import { Route, Routes } from "react-router-dom";
import AdminPanel from "./components/pages/admin-panel/AdminPanel";
import Home from "./components/pages/Home";
import Navbar from "./components/navbar/Navbar";
import DetailsPage from "./components/pages/details/DetailsPage";

function App() {
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
