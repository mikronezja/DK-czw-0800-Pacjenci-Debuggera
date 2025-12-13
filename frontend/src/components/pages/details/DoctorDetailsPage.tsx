import React, { use, useEffect, useState } from "react";
import axios from "axios";
import type { Doctor } from "@/types/doctor";
import { useParams } from "react-router-dom";
import {
  Table,
  TableBody,
  TableCaption,
  TableCell,
  TableFooter,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import styled from "styled-components";

const TableStyled = styled(Table)`
  justify-content: center;
  align-items: center;
  display: flex;
  flex-direction: column;
  padding: 30px;
  margin: 30px auto;
  min-width: 400px;
  width: auto;
`;

const formatSpecialization = (specialization: string): string => {
  const mapping: Record<string, string> = {
    KARDIOLOG: "Kardiolog",
    DERMATOLOG: "Dermatolog",
    NEUROLOG: "Neurolog",
    OKULISTA: "Okulista",
    ORTOPEDA: "Ortopeda",
    CHIRURG: "Chirurg",
    PEDIATRA: "Pediatra",
  };
  return mapping[specialization] || specialization;
};

const DoctorDetailsPage = () => {
  const { idValue } = useParams();
  const [doctor, setDoctor] = useState<Doctor>({
    id: 0,
    name: "",
    surname: "",
    specialization: "",
    pesel: "",
    address: "",
  });

  const getDetails = () => {
    axios
      .get(`http://localhost:8080/doctors/${idValue}`)
      .then((res) => {
        console.log("worked!", res.data);
        setDoctor(res.data);
      })
      .catch((err) => console.error(err));
  };
  useEffect(() => {
    getDetails();
  }, []);

  return (
    <TableStyled>
      <div>
        <TableHeader>
          <TableRow>
            <TableHead>Imię</TableHead>
            <TableHead className="w-[100px]">Nazwisko</TableHead>
            <TableHead>Specjalizacja</TableHead>
            <TableHead>Adres</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          <TableRow>
            <TableCell>{doctor.name}</TableCell>
            <TableCell>{doctor.surname}</TableCell>
            <TableCell>{formatSpecialization(doctor.specialization)}</TableCell>
            <TableCell>{doctor.address}</TableCell>
          </TableRow>
        </TableBody>
      </div>
    </TableStyled>
  );
};

export default DoctorDetailsPage;
