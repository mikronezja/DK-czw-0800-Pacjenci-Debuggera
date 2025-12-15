import React, { useState } from "react";
import NewDoctorPanel from "./NewDoctorPanel";
import DoctorDisplay from "./DoctorDisplay";
import type { Doctor } from "@/types/doctor";
import { Button } from "@/components/ui/button";
import { Plus } from "lucide-react";
import styled from "styled-components";

const AdminPanelContainer = styled.div`
  display: inline-block;
  padding: 30px;
  border: 1px solid #ccc;
  border-radius: 8px;
  margin-top: 30px;
  max-height: 400px;
  min-width: 300px;
  overflow-y: auto;
`;

const CenteredAdminPanelContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
`;

const AdminPanel = () => {
  const [addDoctorOpen, setAddDoctorOpen] = useState(false);
  const [dataArray, setDataArray] = useState<Doctor[]>([]);

  return (
    <CenteredAdminPanelContainer>
      <AdminPanelContainer>
        {!addDoctorOpen && (
          <Button
            variant="outline"
            size="icon"
            aria-label="Add"
            onClick={() => setAddDoctorOpen(true)}
          >
            <Plus />
          </Button>
        )}
        <div>
          {addDoctorOpen ? (
            <NewDoctorPanel
              dataArray={dataArray}
              setDataArray={setDataArray}
              setAddDoctorOpen={setAddDoctorOpen}
            />
          ) : (
            <DoctorDisplay dataArray={dataArray} setDataArray={setDataArray} />
          )}
        </div>
      </AdminPanelContainer>
    </CenteredAdminPanelContainer>
  );
};

export default AdminPanel;
