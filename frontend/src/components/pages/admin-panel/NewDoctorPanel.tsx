import React, { useState } from "react";
import ActionButton from "./ActionButton";

const NewDoctorPanel = () => {
  const [formData, setFormData] = useState({
    name: "",
    surname: "",
    pesel: "",
    specialization: "",
    address: "",
  });

  const saveDoctor = (e: React.SyntheticEvent) => {
    e.preventDefault();
  };
  const deleteDoctor = () => {
    setFormData({
      name: "",
      surname: "",
      pesel: "",
      specialization: "",
      address: "",
    });
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
