import React, { useEffect, useState } from "react";
import axios from "axios";
import { data, useNavigate } from "react-router-dom";
import type { Doctor } from "@/types/doctor";
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
import { Button } from "@/components/ui/button";

interface DoctorDisplayProps {
  dataArray: Array<Doctor>;
  setDataArray: React.Dispatch<React.SetStateAction<Array<Doctor>>>;
}

const TableStyled = styled(Table)`
  overflow-y: auto;
  height: 100%;
`;

const DoctorDisplay = ({ dataArray, setDataArray }: DoctorDisplayProps) => {
  const navigate = useNavigate();
  const deleteDoctor = (id: number) => {
    axios
      .delete(`http://localhost:8080/doctors/${id}`)
      .then((res) => {
        console.log("worked!", res.data);
      })
      .catch((err) => console.error(err));
    setDataArray(
      dataArray.filter((doctor: { id: number }) => doctor.id !== id)
    );
  };

  const getDetailsPage = (id: number) => {
    navigate(`/details/${id}`);
  };

  const fetchData = () => {
    axios
      .get("http://localhost:8080/doctors")
      .then((res) => {
        console.log("worked!", res.data);
        console.log("Dane z backendu:", res.data);
        console.log("Pierwsze id:", res.data[0]?.id); // Sprawdź pierwsze id
        setDataArray(res.data);
      })
      .catch((err) => console.error(err));
  };

  useEffect(() => {
    fetchData();
  }, []);

  // map -> doctors for each doctor i can display
  return (
    <TableStyled>
      <TableCaption>Lista lekarzy</TableCaption>
      <TableHeader>
        <TableRow>
          <TableHead>Imię</TableHead>
          <TableHead className="w-[100px]">Nazwisko</TableHead>
          <TableHead className="text-right">Specjalizacja</TableHead>
          <TableHead className="text-right"></TableHead>
          <TableHead className="text-right"></TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>
        {dataArray.map(({ name, surname, specialization, id }, index) => (
          <TableRow key={index}>
            <TableCell>{name}</TableCell>
            <TableCell>{surname}</TableCell>
            <TableCell className="text-right">{specialization}</TableCell>
            <TableCell className="text-right">
              <Button
                variant="outline"
                size="sm"
                onClick={() => getDetailsPage(id)}
              >
                Szczegóły
              </Button>
            </TableCell>
            <TableCell className="text-right">
              <Button
                variant="outline"
                size="sm"
                onClick={() => deleteDoctor(id)}
              >
                Usuń
              </Button>
            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </TableStyled>
  );
};
// /doctors/id=5 <- get
// /doctors/id=5 <- delete
export default DoctorDisplay;
