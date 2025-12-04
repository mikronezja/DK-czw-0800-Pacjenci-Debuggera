import React, { use, useEffect, useState } from "react";
import axios from "axios";
import type { Doctor } from "@/types/doctor";
import { useParams } from "react-router-dom";

const DetailsPage = () => {
  const { idValue } = useParams();
  const [doctor, setDoctor] = useState<Doctor>({
    id: 0,
    name: "",
    surname: "",
    specialization: "",
    pesel: "",
    address: "",
  });
  const getDetails = (id: number) => {
    axios
      .get(`http://localhost:8080/doctors/${idValue}`)
      .then((res) => {
        console.log("worked!", res.data);
        setDoctor(res.data);
      })
      .catch((err) => console.error(err));
  };
  useEffect(() => {
    getDetails(Number(idValue)!);
  }, []);
  return (
    <div>
      {`Szczegóły dla dr. ${doctor.name} ${doctor.surname}`}
      {`Specjalizacja: ${doctor.specialization}`}
      {`Adres: ${doctor.address}`}
    </div>
  );
};

export default DetailsPage;
