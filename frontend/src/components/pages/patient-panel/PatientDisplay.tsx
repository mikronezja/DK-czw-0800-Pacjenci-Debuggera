import React, { useEffect, useState } from "react";
import axios from "axios";
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
import { PATIENT_DETAILS_ROUTE } from "@/text/routes";
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
  const [loading, setLoading] = useState(false);

  const deletePacient = (id: number) => {
    if (!window.confirm("Czy na pewno chcesz usunąć tego pacjenta?")) {
      return;
    }
    
    axios
      .delete(`http://localhost:8080/patients/${id}`)
      .then(() => {
        fetchData();
      })
      .catch((err) => {
        console.error(err);
        const errorMessage = err.response?.data || err.message || "Błąd podczas usuwania pacjenta!";
        alert(typeof errorMessage === 'string' ? errorMessage : "Błąd podczas usuwania pacjenta!");
      });
  };
  const getDetailsPage = (id: number) => {
    navigate(`${PATIENT_DETAILS_ROUTE}/${id}`);
  };

  const fetchData = () => {
    setLoading(true);
    axios
      .get("http://localhost:8080/patients", { timeout: 10000 })
      .then((res: any) => {
        if (res.data && Array.isArray(res.data)) {
          setDataArray(res.data);
        } else {
          setDataArray([]);
        }
        setLoading(false);
      })
      .catch((err: any) => {
        console.error(err);
        setLoading(false);
        if (err.code === 'ECONNABORTED' || err.message.includes('timeout')) {
          alert("Timeout - serwer nie odpowiada!");
        } else if (err.code === 'ERR_NETWORK' || err.message.includes('Network Error')) {
          alert("Błąd połączenia z serwerem!");
        } else {
          alert("Błąd podczas pobierania listy pacjentów!");
        }
        setDataArray([]);
      });
  };

  useEffect(() => {
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
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
        {loading && (
          <TableRow>
            <TableCell colSpan={4} style={{ textAlign: "center" }}>
              Ładowanie...
            </TableCell>
          </TableRow>
        )}
        {!loading && dataArray.length === 0 && (
          <TableRow>
            <TableCell colSpan={4} style={{ textAlign: "center" }}>
              Brak pacjentów w bazie
            </TableCell>
          </TableRow>
        )}
        {!loading && dataArray.map(({ name, surname, id }) => (
          <TableRow key={id}>
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
