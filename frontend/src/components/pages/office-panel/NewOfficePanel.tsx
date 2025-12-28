import type { Office } from "@/types/office";
import React, { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import styled from "styled-components";
import { callAddOffice } from "@/api/office_calls";

interface OfficeDisplayProps {
  dataArray: Array<Office>;
  setDataArray: React.Dispatch<React.SetStateAction<Array<Office>>>;
  setAddOfficeOpen: React.Dispatch<React.SetStateAction<boolean>>;
}

const NewOfficeStyled = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin-top: 20px;
  border: 1px solid #ccc;
  padding: 20px;
  border-radius: 8px;

  gap: 15px;

  /* Chrome, Safari, Edge, Opera */
  input::-webkit-outer-spin-button,
  input::-webkit-inner-spin-button {
    -webkit-appearance: none;
    margin: 0;
  }

  /* Firefox */
  input[type="number"] {
    -moz-appearance: textfield;
  }
`;

const NewOfficePanel = ({
  dataArray,
  setDataArray,
  setAddOfficeOpen,
}: OfficeDisplayProps) => {
  const [formData, setFormData] = useState({
    roomNumber: 0,
    shifts: [],
  });

  const addOffice = async (e: React.SyntheticEvent) => {
    e.preventDefault();

    try {
      const response = await callAddOffice(formData);

      setDataArray([...dataArray, { ...formData, id: response.data.id }]);
    } catch (err) {
      console.log(err);
    }
    setAddOfficeOpen(false);
  };
  const deleteOffice = () => {
    setFormData({
      roomNumber: 0,
      shifts: [],
    });
    setAddOfficeOpen(false);
  };
  return (
    <NewOfficeStyled>
      <Input
        type="number"
        placeholder="Room number"
        onChange={(e) => {
          setFormData({ ...formData, roomNumber: Number(e.target.value) });
        }}
      />
      <Button variant="outline" size="sm" onClick={addOffice}>
        Zapisz
      </Button>
      <Button variant="outline" size="sm" onClick={deleteOffice}>
        Anuluj
      </Button>
    </NewOfficeStyled>
  );
};

export default NewOfficePanel;
