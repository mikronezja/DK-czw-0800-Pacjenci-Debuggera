import { useEffect, useState } from "react";
import type { Doctor } from "@/types/doctor";
import { useParams } from "react-router-dom";
import {
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { callGetDoctorById } from "@/api/doctor_calls";
import { SPECIALIZATIONS } from "@/constants/specializations";
import { TableDetailsStyled } from "@/styles/styledcomponent";

const formatSpecialization = (specialization: string): string => {
  return SPECIALIZATIONS[specialization] || specialization;
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

  const getDetails = async () => {
    try {
      const response = await callGetDoctorById(Number(idValue));

      setDoctor(response.data);
    } catch (err) {
      console.error("Error fetching doctors:", err);
    }
  };
  useEffect(() => {
    getDetails();
  }, []);

  return (
    <TableDetailsStyled>
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
    </TableDetailsStyled>
  );
};

export default DoctorDetailsPage;
