import type { Office } from "@/types/office";
import React, { useState } from "react";
import axios from "axios";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import styled from "styled-components";

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

  const saveOffice = (e: React.SyntheticEvent) => {
    e.preventDefault();

    axios
      .post("http://localhost:8080/offices/add", formData)
      .then((res) => {
        console.log("Office saved:", res.data);
        setDataArray([...dataArray, { ...formData, id: res.data.id }]);
      })
      .catch((err) => {
        console.log(formData);
        console.error("Error saving office:", err);
      });
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
      <Button variant="outline" size="sm" onClick={saveOffice}>
        Zapisz
      </Button>
      <Button variant="outline" size="sm" onClick={deleteOffice}>
        Anuluj
      </Button>
    </NewOfficeStyled>
  );
};

export default NewOfficePanel;
