import React, { useState } from "react";
import axios from "axios";
import type { Doctor } from "@/types/doctor";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import styled from "styled-components";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

interface DoctorDisplayProps {
  dataArray: Array<Doctor>;
  setDataArray: React.Dispatch<React.SetStateAction<Array<Doctor>>>;
  setAddDoctorOpen: React.Dispatch<React.SetStateAction<boolean>>;
  onDoctorAdded?: () => void;
}

const FormStyled = styled.form`
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  gap: 10px;
`;

const NewDoctorPanel = ({
  dataArray,
  setDataArray,
  setAddDoctorOpen,
  onDoctorAdded,
}: DoctorDisplayProps) => {
  const [formData, setFormData] = useState({
    name: "",
    surname: "",
    pesel: "",
    specialization: "",
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

  const saveDoctor = (e: React.SyntheticEvent) => {
    e.preventDefault();

    const trimmedName = formData.name.trim();
    const trimmedSurname = formData.surname.trim();
    const trimmedPesel = formData.pesel.trim();
    const trimmedAddress = formData.address.trim();

    // Validate required fields
    if (!trimmedName || !trimmedSurname || !formData.specialization) {
      alert("Imię, nazwisko i specjalizacja są wymagane!");
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
      .post("http://localhost:8080/doctors/add", trimmedData, { timeout: 10000 })
      .then((res: any) => {
        if (res.data && res.data.id) {
          setDataArray([...dataArray, { ...trimmedData, id: res.data.id }]);
          setAddDoctorOpen(false);
          if (onDoctorAdded) {
            onDoctorAdded();
          }
        }
      })
      .catch((err: any) => {
        console.error("Error saving doctor:", err);
        const errorMessage = err.response?.data || err.message || "Błąd podczas zapisywania lekarza!";
        alert(typeof errorMessage === 'string' ? errorMessage : "Błąd podczas zapisywania lekarza!");
      });
  };
  const deleteDoctor = () => {
    setFormData({
      name: "",
      surname: "",
      pesel: "",
      specialization: "",
      address: "",
    });
    setAddDoctorOpen(false);
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
      <Label style={{ display: "flex", flexDirection: "column" }}>
        Specjalizacja:
        <Select
          value={formData.specialization}
          onValueChange={(value) => {
            setFormData({ ...formData, specialization: value });
          }}
        >
          <SelectTrigger>
            <SelectValue placeholder="Wybierz specjalizację" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="KARDIOLOG">Kardiolog</SelectItem>
            <SelectItem value="DERMATOLOG">Dermatolog</SelectItem>
            <SelectItem value="NEUROLOG">Neurolog</SelectItem>
            <SelectItem value="OKULISTA">Okulista</SelectItem>
            <SelectItem value="ORTOPEDA">Ortopeda</SelectItem>
            <SelectItem value="CHIRURG">Chirurg</SelectItem>
            <SelectItem value="PEDIATRA">Pediatra</SelectItem>
          </SelectContent>
        </Select>
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

      <Button variant="outline" size="sm" onClick={saveDoctor}>
        Zapisz
      </Button>
      <Button variant="outline" size="sm" onClick={deleteDoctor}>
        Anuluj
      </Button>
    </FormStyled>
  );
};

export default NewDoctorPanel;
