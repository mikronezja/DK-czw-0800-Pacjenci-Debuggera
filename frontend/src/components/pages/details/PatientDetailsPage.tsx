import axios from "axios";
import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import styled from "styled-components";
import {
  Table,
  TableBody,
  TableCaption,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";

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

const PatientDetailsPage = () => {
  const { idValue } = useParams();
  const [pacient, setPacient] = useState({
    name: "",
    surname: "",
    pesel: "",
    address: "",
    id: 0,
  });

  useEffect(() => {
    if (!idValue) {
      alert("Brak ID pacjenta!");
      return;
    }
    
    const id = Number(idValue);
    if (isNaN(id) || id <= 0) {
      alert("Nieprawidłowe ID pacjenta!");
      return;
    }

    axios
      .get(`http://localhost:8080/patients/${id}`, { timeout: 10000 })
      .then((res: any) => {
        if (res.data) {
          setPacient(res.data);
        } else {
          alert("Pacjent nie został znaleziony!");
        }
      })
      .catch((err: any) => {
        console.error(err);
        if (err.response?.status === 404) {
          alert("Pacjent nie został znaleziony!");
        } else if (err.code === 'ECONNABORTED' || err.message?.includes('timeout')) {
          alert("Timeout - serwer nie odpowiada!");
        } else if (err.code === 'ERR_NETWORK' || err.message?.includes('Network Error')) {
          alert("Błąd połączenia z serwerem!");
        } else {
          alert("Błąd podczas pobierania danych pacjenta!");
        }
      });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [idValue]);
  return (
    <TableStyled>
      <div>
        <TableHeader>
          <TableRow>
            <TableHead>Imię</TableHead>
            <TableHead className="w-[100px]">Nazwisko</TableHead>
            <TableHead>Adres</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          <TableRow>
            <TableCell>{pacient.name}</TableCell>
            <TableCell>{pacient.surname}</TableCell>
            <TableCell>{pacient.address}</TableCell>
          </TableRow>
        </TableBody>
      </div>
    </TableStyled>
  );
};

export default PatientDetailsPage;
