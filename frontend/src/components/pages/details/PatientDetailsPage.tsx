import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import {
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { callGetPatientById } from "@/api/patient_calls";
import { TableDetailsStyled } from "@/styles/styledcomponent";

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
    try {
      const response = await callGetPatientById(Number(idValue));

      setPacient(response.data);
    } catch (err) {
      console.log(err);
    }
  };

  useEffect(() => {
    fetchPatients();
  }, []);
  return (
    <TableDetailsStyled>
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
    </TableDetailsStyled>
  );
};

export default PatientDetailsPage;
