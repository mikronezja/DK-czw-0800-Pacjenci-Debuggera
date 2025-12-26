import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import styled from "styled-components";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { callGetPatientById } from "@/api/patient_calls";

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

  const fetchPatients = async () => {
    try 
    {
      const response = await callGetPatientById(Number(idValue))

       setPacient(response.data)
    } catch (err)
    {
      console.log(err)
    }
  }

  useEffect(() => {
    fetchPatients();
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
