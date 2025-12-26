import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import type { Pacient } from "@/types/pacient";
import {
  Table,
  TableBody,
  TableCaption,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import styled from "styled-components";
import { Button } from "@/components/ui/button";
import { Eye, X } from "lucide-react";
import { PATIENT_DETAILS_ROUTE } from "@/constants/routes";
import { callDeletePatient, callGetPatients } from "@/api/patient_calls";
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

  const fetchPatients = async () => {
    try {
      const response = await callGetPatients();

      setDataArray(response.data);
    } catch (err) {
      console.log(err);
    }
  };

  const deletePacient = async (id: number) => {
    try {
      await callDeletePatient(id);

      setDataArray(
        dataArray.filter((pacient: { id: number }) => pacient.id !== id)
      );
    } catch (err) {
      console.log(err);
    }
  };
  const getDetailsPage = (id: number) => {
    navigate(`${PATIENT_DETAILS_ROUTE}/${id}`);
  };

  useEffect(() => {
    fetchPatients();
  }, []);

  return (
    <TableStyled>
      <TableCaption>Lista pacjentów</TableCaption>
      <TableHeader>
        <TableRow>
          <TableHead>Imię</TableHead>
          <TableHead className="w-[100px]">Nazwisko</TableHead>
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
                onClick={() => getDetailsPage(id)}
              >
                <Eye />
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
