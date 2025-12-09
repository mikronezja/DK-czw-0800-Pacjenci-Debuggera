import React, { useState } from "react";
import NewDoctorPanel from "./NewDoctorPanel";
import DoctorDisplay from "./DoctorDisplay";
import type { Doctor } from "@/types/doctor";

const AdminPanel = () => {
  const [addDoctorOpen, setAddDoctorOpen] = useState(false);
  const [dataArray, setDataArray] = useState<Doctor[]>([]);

  return (
    <div>
      Admin Panel Page
      <button onClick={() => setAddDoctorOpen(true)}>Dodaj</button>
      <div style={{ display: "flex", flexDirection: "column", gap: "50px" }}>
        {addDoctorOpen && (
          <NewDoctorPanel
            dataArray={dataArray}
            setDataArray={setDataArray}
            setAddDoctorOpen={setAddDoctorOpen}
          />
        )}
        <DoctorDisplay dataArray={dataArray} setDataArray={setDataArray} />
      </div>
    </div>
  );
};

export default AdminPanel;
