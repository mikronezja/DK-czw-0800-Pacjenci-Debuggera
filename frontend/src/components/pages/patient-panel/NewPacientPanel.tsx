import React, { useState } from "react";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import { Label } from "@/components/ui/label";
import type { Pacient } from "@/types/pacient";
import { callAddPatient } from "@/api/patient_calls";
import { FormStyled } from "@/styles/styledcomponent";

interface PacientDisplayProps {
  dataArray: Array<Pacient>;
  setDataArray: React.Dispatch<React.SetStateAction<Array<Pacient>>>;
  setAddPacientOpen: React.Dispatch<React.SetStateAction<boolean>>;
}

const NewPacientPanel = ({
  dataArray,
  setDataArray,
  setAddPacientOpen,
}: PacientDisplayProps) => {
  const [formData, setFormData] = useState({
    name: "",
    surname: "",
  });

  const addPacient = async (e: React.SyntheticEvent) => {
    e.preventDefault();

    try {
      const response = await callAddPatient(formData);

      setDataArray([...dataArray, { ...formData, id: response.data.id }]);
    } catch (err) {
      console.log(err);
    }

    setAddPacientOpen(false);
  };
  const deletePacient = () => {
    setFormData({
      name: "",
      surname: "",
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
      <Button variant="outline" size="sm" onClick={addPacient}>
        Zapisz
      </Button>
      <Button variant="outline" size="sm" onClick={deletePacient}>
        Anuluj
      </Button>
    </FormStyled>
  );
};

export default NewPacientPanel;
