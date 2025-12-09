import React, { useState } from "react";
import ActionButton from "./ActionButton";
import axios from "axios";
import type { Doctor } from "@/types/doctor";

interface DoctorDisplayProps {
  dataArray: Array<Doctor>;
  setDataArray: React.Dispatch<React.SetStateAction<Array<Doctor>>>;
  setAddDoctorOpen: React.Dispatch<React.SetStateAction<boolean>>;
}

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
    <form
      style={{
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        flexDirection: "column",
        gap: "10px",
      }}
    >
      <label style={{ display: "flex", flexDirection: "column" }}>
        Imię:
        <textarea
          value={formData.name}
          onChange={(e) => {
            setFormData({ ...formData, name: e.target.value });
          }}
        />
      </label>
      <label style={{ display: "flex", flexDirection: "column" }}>
        Nazwisko:
        <textarea
          value={formData.surname}
          onChange={(e) => {
            setFormData({ ...formData, surname: e.target.value });
          }}
        />
      </label>
      <label style={{ display: "flex", flexDirection: "column" }}>
        PESEL:
        <textarea
          value={formData.pesel}
          onChange={(e) => {
            setFormData({ ...formData, pesel: e.target.value });
          }}
        />
      </label>
      <label style={{ display: "flex", flexDirection: "column" }}>
        Specjalizacja:
        <textarea
          value={formData.specialization}
          onChange={(e) => {
            setFormData({ ...formData, specialization: e.target.value });
          }}
        />
      </label>
      <label style={{ display: "flex", flexDirection: "column" }}>
        Adres:
        <textarea
          value={formData.address}
          onChange={(e) => {
            setFormData({ ...formData, address: e.target.value });
          }}
        />
      </label>

      <ActionButton handleClick={saveDoctor} label="Zapisz" />
      <ActionButton handleClick={deleteDoctor} label="Usuń" />
    </form>
  );
};

export default NewDoctorPanel;
