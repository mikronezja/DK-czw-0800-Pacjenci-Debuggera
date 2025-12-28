import React, { useState } from "react";
import axios from "axios";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import styled from "styled-components";
import { Label } from "@/components/ui/label";
import type { Pacient } from "@/types/pacient";

const FormStyled = styled.form`
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  gap: 10px;
`;
interface PacientDisplayProps {
  dataArray: Array<Pacient>;
  setDataArray: React.Dispatch<React.SetStateAction<Array<Pacient>>>;
  setAddPacientOpen: React.Dispatch<React.SetStateAction<boolean>>;
  onPatientAdded?: () => void;
}

const NewPacientPanel = ({
  dataArray,
  setDataArray,
  setAddPacientOpen,
  onPatientAdded,
}: PacientDisplayProps) => {
  const [formData, setFormData] = useState({
    name: "",
    surname: "",
    pesel: "",
    address: "",
  });

  const validatePESEL = (pesel: string): boolean => {
    if (!pesel || pesel.trim().length === 0) {
      return true; // PESEL is optional
    }
    const trimmed = pesel.trim();
    if (trimmed.length !== 11) {
      return false;
    }
    if (!/^\d+$/.test(trimmed)) {
      return false;
    }
    return true;
  };

  const savePacient = (e: React.SyntheticEvent) => {
    e.preventDefault();

    const trimmedName = formData.name.trim();
    const trimmedSurname = formData.surname.trim();
    const trimmedPesel = formData.pesel.trim();
    const trimmedAddress = formData.address.trim();

    // Validate required fields
    if (!trimmedName || !trimmedSurname) {
      alert("Imię i nazwisko są wymagane!");
      return;
    }

    // Validate field lengths
    if (trimmedName.length > 100) {
      alert("Imię jest zbyt długie (maksymalnie 100 znaków)!");
      return;
    }
    if (trimmedSurname.length > 100) {
      alert("Nazwisko jest zbyt długie (maksymalnie 100 znaków)!");
      return;
    }
    if (trimmedAddress.length > 200) {
      alert("Adres jest zbyt długi (maksymalnie 200 znaków)!");
      return;
    }

    // Validate PESEL format
    if (!validatePESEL(trimmedPesel)) {
      alert("PESEL musi składać się z dokładnie 11 cyfr!");
      return;
    }

    // Trim data before sending
    const trimmedData = {
      ...formData,
      name: trimmedName,
      surname: trimmedSurname,
      pesel: trimmedPesel,
      address: trimmedAddress,
    };
    
    axios
      .post("http://localhost:8080/patients/add", trimmedData, { timeout: 10000 })
      .then((res: any) => {
        if (res.data && res.data.id) {
          setDataArray([...dataArray, { ...trimmedData, id: res.data.id }]);
          setAddPacientOpen(false);
          if (onPatientAdded) {
            onPatientAdded();
          }
        }
      })
      .catch((err: any) => {
        console.error("Error saving pacient:", err);
        const errorMessage = err.response?.data || err.message || "Błąd podczas zapisywania pacjenta!";
        alert(typeof errorMessage === 'string' ? errorMessage : "Błąd podczas zapisywania pacjenta!");
      });
  };
  const deletePacient = () => {
    setFormData({
      name: "",
      surname: "",
      pesel: "",
      address: "",
    });
    setAddPacientOpen(false);
  };

  return (
    <FormStyled>
      <Label style={{ display: "flex", flexDirection: "column" }}>
        Imię:
        <Textarea
          value={formData.name}
          className="h-1"
          onChange={(e) => {
            setFormData({ ...formData, name: e.target.value });
          }}
        />
      </Label>
      <Label style={{ display: "flex", flexDirection: "column" }}>
        Nazwisko:
        <Textarea
          value={formData.surname}
          onChange={(e) => {
            setFormData({ ...formData, surname: e.target.value });
          }}
        />
      </Label>
      <Label style={{ display: "flex", flexDirection: "column" }}>
        <div>PESEL:</div>
        <Textarea
          value={formData.pesel}
          onChange={(e) => {
            setFormData({ ...formData, pesel: e.target.value });
          }}
        />
      </Label>
      <Label
        className="text-left"
        style={{ display: "flex", flexDirection: "column" }}
      >
        Adres:
        <Textarea
          value={formData.address}
          onChange={(e) => {
            setFormData({ ...formData, address: e.target.value });
          }}
        />
      </Label>

      <Button variant="outline" size="sm" onClick={savePacient}>
        Zapisz
      </Button>
      <Button variant="outline" size="sm" onClick={deletePacient}>
        Anuluj
      </Button>
    </FormStyled>
  );
};

export default NewPacientPanel;
