import axios from "axios";
import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import styled from "styled-components";
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
    axios
      .get(`http://localhost:8080/patients/${idValue}`)
      .then((res) => {
        console.log("worked!", res.data);
        setPacient(res.data);
      })
      .catch((err) => console.error(err));
  }, []);
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
