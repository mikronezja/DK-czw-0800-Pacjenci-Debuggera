import React, { useEffect, useState } from "react";
import axios from "axios";
import { data, useNavigate } from "react-router-dom";
import type { Doctor } from "@/types/doctor";

interface DoctorDisplayProps {
  dataArray: Array<Doctor>;
  setDataArray: React.Dispatch<React.SetStateAction<Array<Doctor>>>;
}

const DoctorDisplay = ({ dataArray, setDataArray }: DoctorDisplayProps) => {
  const navigate = useNavigate();
  const deleteDoctor = (id: string) => {
    axios
      .delete(`http://localhost:8080/doctors/id=${id}`)
      .then((res) => {
        console.log("worked!", res.data);
        setDataArray(res.data);
      })
      .catch((err) => console.error(err));
    setDataArray(
      dataArray.filter((doctor: { id: string }) => doctor.id !== id)
    );
  };

  const getDetailsPage = (id: string) => {
    navigate(`/details/id=${id}`);
  };

  const fetchData = () => {
    axios
      .get("http://localhost:8080/doctors")
      .then((res) => {
        console.log("worked!", res.data);
        setDataArray(res.data);
      })
      .catch((err) => console.error(err));
  };

  useEffect(() => {
    fetchData();
  }, []);

  // map -> doctors for each doctor i can display
  return (
    <div
      style={{
        display: "flex",
        flexDirection: "column",
        gap: "10px",
        justifyContent: "center",
        alignItems: "center",
      }}
    >
      {dataArray.map(({ name, surname, specialization, id }, index) => (
        <div key={index} style={{ display: "flex", gap: "10px" }}>
          {name} {surname} {specialization} {id}
          <button
            onClick={() => {
              getDetailsPage(id);
            }}
          >
            Szczegóły{" "}
          </button>
          <button
            onClick={() => {
              deleteDoctor(id);
            }}
          >
            Usuń
          </button>
        </div>
      ))}
    </div>
  );
};
// /doctors/id=5 <- get
// /doctors/id=5 <- delete
export default DoctorDisplay;
