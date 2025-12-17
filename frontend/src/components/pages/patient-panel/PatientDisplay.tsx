import React, { useEffect, useState } from "react";
import axios from "axios";
import { data, useNavigate } from "react-router-dom";
import type { Doctor } from "@/types/doctor";
import type { Pacient } from "@/types/pacient";
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
import { Eye, X } from "lucide-react";
// import { PATIENT_DETAILS_ROUTE } from "@/text/navbar";

interface PacientDisplayProps {
  dataArray: Array<Pacient>;
  setDataArray: React.Dispatch<React.SetStateAction<Array<Pacient>>>;
}

const TableStyled = styled(Table)`
  overflow-y: auto;
  height: 100%;
`;

const PatientDisplay = ({ dataArray, setDataArray }: PacientDisplayProps) => {
  const navigate = useNavigate();

  const deletePacient = (id: number) => {
    setDataArray(
      dataArray.filter((doctor: { id: number }) => doctor.id !== id)
    );
  };
  // const getDetailsPage = (id: number) => {
  //   navigate(`${PATIENT_DETAILS_ROUTE}/${id}`);
  // };

  return (
    <TableStyled>
      <TableCaption>Lista pacjentów</TableCaption>
      <TableHeader>
        <TableRow>
          <TableHead>Imię</TableHead>
          <TableHead className="w-[100px]">Nazwisko</TableHead>
          <TableHead className="text-right"></TableHead>
          <TableHead className="text-right"></TableHead>
          <TableHead className="text-right"></TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>
        {dataArray.map(({ name, surname, id }, index) => (
          <TableRow key={index}>
            <TableCell>{name}</TableCell>
            <TableCell>{surname}</TableCell>
            <TableCell className="text-right">
              <Button
                variant="outline"
                size="sm"
                className="rounded-full w-8 h-8"
                // onClick={() => getDetailsPage(id)}
              >
                <Eye />
              </Button>
            </TableCell>
            <TableCell className="text-right">
              <Button variant="outline" size="sm">
                Umów
              </Button>
            </TableCell>
            <TableCell className="text-right">
              <Button
                variant="outline"
                size="sm"
                className="rounded-full w-8 h-8"
                onClick={() => deletePacient(id)}
              >
                <X />
              </Button>
            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </TableStyled>
  );
};

export default PatientDisplay;
