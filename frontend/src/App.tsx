import { Route, Routes } from "react-router-dom";
import Home from "./components/pages/Home";
import Navbar from "./components/navbar/Navbar";
import { Switch } from "./components/ui/switch";
import { useState } from "react";
import DoctorPanel from "./components/pages/doctor-panel/DoctorPanel";
import {
  DOCTOR_DETAILS_ROUTE,
  DOCTOR_PANEL_ROUTE,
  HOME_ROUTE,
  OFFICE_PANEL_ROUTE,
  PATIENT_DETAILS_ROUTE,
  PATIENT_PANEL_ROUTE,
  OFFICE_DETAILS_ROUTE,
  DOCTOR_SHIFT_PANEL_ROUTE,
  OFFICE_SHIFT_PANEL_ROUTE,
} from "./constants/routes";
import PatientPanel from "./components/pages/patient-panel/PatientPanel";
import DoctorDetailsPage from "./components/pages/details/DoctorDetailsPage";
import PatientDetailsPage from "./components/pages/details/PatientDetailsPage";
import OfficePanel from "./components/pages/office-panel/OfficePanel";
import ShiftPanel from "./components/pages/shift-panel/ShiftDoctorPanel";
import OfficeDetailsPage from "./components/pages/details/OfficeDetailsPage";
import ShiftDoctorPanel from "./components/pages/shift-panel/ShiftDoctorPanel";
import ShiftOfficePanel from "./components/pages/shift-panel/ShiftOfficePanel";

function App() {
  const [checked, setChecked] = useState(false);
  return (
    <>
      <Navbar />
      <Routes>
        <Route path={HOME_ROUTE} element={<Home />} />
        <Route path={DOCTOR_PANEL_ROUTE} element={<DoctorPanel />} />
        <Route path={PATIENT_PANEL_ROUTE} element={<PatientPanel />} />
        <Route path={OFFICE_PANEL_ROUTE} element={<OfficePanel />} />
        <Route
          path={`${DOCTOR_SHIFT_PANEL_ROUTE}/:idValue`}
          element={<ShiftDoctorPanel />}
        />

        <Route
          path={`${OFFICE_SHIFT_PANEL_ROUTE}/:idValue`}
          element={<ShiftOfficePanel />}
        />

        {/* ///////// */}
        <Route
          path={`${DOCTOR_DETAILS_ROUTE}/:idValue`}
          element={<DoctorDetailsPage />}
        />
        <Route
          path={`${OFFICE_DETAILS_ROUTE}/:idValue`}
          element={<OfficeDetailsPage />}
        />
        <Route
          path={`${PATIENT_DETAILS_ROUTE}/:idValue`}
          element={<PatientDetailsPage />}
        />
      </Routes>
    </>
  );
}

export default App;
