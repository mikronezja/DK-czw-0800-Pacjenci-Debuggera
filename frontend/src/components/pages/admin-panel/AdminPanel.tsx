import React, { useState } from "react";
import NewDoctorPanel from "./NewDoctorPanel";
import DoctorDisplay from "./DoctorDisplay";

const AdminPanel = () => {
  const [addDoctorOpen, setAddDoctorOpen] = useState(false);

  return (
    <div>
      Admin Panel Page
      <button onClick={() => setAddDoctorOpen(true)}>Dodaj</button>
      {addDoctorOpen && <NewDoctorPanel />}
      <DoctorDisplay />
    </div>
  );
};

export default AdminPanel;
