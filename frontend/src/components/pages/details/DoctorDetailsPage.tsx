import React, { useEffect, useState } from "react";
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
    if (!idValue) {
      alert("Brak ID lekarza!");
      return;
    }
    
    const id = Number(idValue);
    if (isNaN(id) || id <= 0) {
      alert("Nieprawidłowe ID lekarza!");
      return;
    }

    axios
      .get(`http://localhost:8080/doctors/${id}`, { timeout: 10000 })
      .then((res: any) => {
        if (res.data) {
          setDoctor(res.data);
        } else {
          alert("Lekarz nie został znaleziony!");
        }
      })
      .catch((err: any) => {
        console.error(err);
        if (err.response?.status === 404) {
          alert("Lekarz nie został znaleziony!");
        } else if (err.code === 'ECONNABORTED' || err.message?.includes('timeout')) {
          alert("Timeout - serwer nie odpowiada!");
        } else if (err.code === 'ERR_NETWORK' || err.message?.includes('Network Error')) {
          alert("Błąd połączenia z serwerem!");
        } else {
          alert("Błąd podczas pobierania danych lekarza!");
        }
      });
  };
  
  useEffect(() => {
    getDetails();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [idValue]);

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
