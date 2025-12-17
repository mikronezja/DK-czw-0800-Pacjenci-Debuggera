import type { Pacient } from "@/types/pacient";
import React, { useState } from "react";
import PatientDisplay from "./PatientDisplay";
import styled from "styled-components";
import { Plus } from "lucide-react";
import { Button } from "@/components/ui/button";
import NewPacientPanel from "./NewPacientPanel";

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
  const [dataArray, setDataArray] = useState<Pacient[]>([]);
  const [addPatientOpen, setAddPatientOpen] = useState(false);

  return (
    <CenteredPatientPanelContainer>
      <PatientPanelContainer>
        {addPatientOpen ? (
          <NewPacientPanel
            dataArray={dataArray}
            setDataArray={setDataArray}
            setAddPacientOpen={setAddPatientOpen}
          />
        ) : (
          <div>
            <Button
              variant="outline"
              size="icon"
              aria-label="Add"
              onClick={() => setAddPatientOpen(true)}
            >
              <Plus />
            </Button>
            <PatientDisplay dataArray={dataArray} setDataArray={setDataArray} />
          </div>
        )}
      </PatientPanelContainer>
    </CenteredPatientPanelContainer>
  );
};

export default PatientPanel;
