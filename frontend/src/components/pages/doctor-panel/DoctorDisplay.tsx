import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import type { Doctor } from "@/types/doctor";
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
import { DOCTOR_DETAILS_ROUTE } from "@/constants/routes";
import { callDeleteDoctor, callGetDoctors } from "@/api/doctor_calls";

interface DoctorDisplayProps {
  dataArray: Array<Doctor>;
  setDataArray: React.Dispatch<React.SetStateAction<Array<Doctor>>>;
}

const TableStyled = styled(Table)`
  overflow-y: auto;
  height: 100%;
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

const DoctorDisplay = ({ dataArray, setDataArray }: DoctorDisplayProps) => {
  const navigate = useNavigate();
  const deleteDoctor = async (id: number) => {
    await callDeleteDoctor(id)
    setDataArray(
      dataArray.filter((doctor: { id: number }) => doctor.id !== id)
    );
  };

  const getDetailsPage = (id: number) => {
    navigate(`${DOCTOR_DETAILS_ROUTE}/${id}`);
  };

  const fetchDoctors = async () => {
    try {
      const response = await callGetDoctors();
      
      setDataArray(response.data); 
    } catch (err) {
      console.error("Error fetching doctors:", err);
    }

  };

  useEffect(() => {
    fetchDoctors();
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
            <TableCell className="text-right">
              {formatSpecialization(specialization)}
            </TableCell>
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
                onClick={() => deleteDoctor(id)}
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
// /doctors/id=5 <- get
// /doctors/id=5 <- delete
export default DoctorDisplay;
