import type { Pacient } from "@/types/doctor";
import React, { useState } from "react";
import PatientDisplay from "./PatientDisplay";
import styled from "styled-components";

const PatientPanelContainer = styled.div`
  display: inline-block;
  padding: 30px;
  border: 1px solid #ccc;
  border-radius: 8px;
  margin-top: 30px;
  max-height: 400px;
  min-width: 300px;
  overflow-y: auto;
`;

const CenteredPatientPanelContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
`;

const PatientPanel = () => {
  const [dataArray, setDataArray] = useState<Pacient[]>([
    {
      name: "Jan",
      surname: "Kowalski",
      pesel: "12345678901",
      address: "ul. Przykładowa 1, 00-001 Warszawa",
      id: 1,
    },
  ]);
  const [addPatientOpen, setAddPatientOpen] = useState(false);

  return (
    <CenteredPatientPanelContainer>
      <PatientPanelContainer>
        {addPatientOpen ? null : (
          <PatientDisplay dataArray={dataArray} setDataArray={setDataArray} />
        )}
      </PatientPanelContainer>
    </CenteredPatientPanelContainer>
  );
};

export default PatientPanel;
