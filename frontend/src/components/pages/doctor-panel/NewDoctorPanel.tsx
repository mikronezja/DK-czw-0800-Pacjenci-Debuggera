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
}: DoctorDisplayProps) => {
  const [formData, setFormData] = useState({
    name: "",
    surname: "",
    pesel: "",
    specialization: "",
    address: "",
  });

  const saveDoctor = (e: React.SyntheticEvent) => {
    e.preventDefault();

    axios
      .post("http://localhost:8080/doctors/add", formData)
      .then((res) => {
        console.log("Doctor saved:", res.data);
        setDataArray([...dataArray, { ...formData, id: res.data.id }]);
      })
      .catch((err) => {
        console.log(formData);
        console.error("Error saving doctor:", err);
      });
    setAddDoctorOpen(false);
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
