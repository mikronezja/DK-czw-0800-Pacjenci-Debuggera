import { Route, Routes } from "react-router-dom";
import Home from "./components/pages/Home";
import Navbar from "./components/navbar/Navbar";
import DetailsPage from "./components/pages/details/DetailsPage";
import { Switch } from "./components/ui/switch";
import { useState } from "react";
import DoctorPanel from "./components/pages/doctor-panel/DoctorPanel";
import {
  DETAILS_ROUTE,
  DOCTOR_PANEL_ROUTE,
  HOME_ROUTE,
  PATIENT_PANEL_ROUTE,
} from "./text/navbar";
import PatientPanel from "./components/pages/patient-panel/PatientPanel";

function App() {
  const [checked, setChecked] = useState(false);
  return (
    <>
      <Navbar />
      <Routes>
        <Route path={HOME_ROUTE} element={<Home />} />
        <Route path={DOCTOR_PANEL_ROUTE} element={<DoctorPanel />} />
        <Route path={`${DETAILS_ROUTE}/:idValue`} element={<DetailsPage />} />
        <Route path={PATIENT_PANEL_ROUTE} element={<PatientPanel />} />
      </Routes>
    </>
  );
}

export default App;
