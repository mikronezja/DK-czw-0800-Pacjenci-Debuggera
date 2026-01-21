import React from "react";
import { useNavigate } from "react-router-dom";
import type { Pacient } from "@/types/pacient";
import {
  TableBody,
  TableCaption,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Button } from "@/components/ui/button";
import { Eye, X } from "lucide-react";
import { APPOINTMENT_ROUTE, PATIENT_DETAILS_ROUTE } from "@/constants/routes";
import { callDeletePatient } from "@/api/patient_calls";
import { TableStyled } from "@/styles/styledcomponent";
import { useGetPatients } from "@/hooks/useGetPatients";
import { toast } from "sonner";
import type { ErrorType } from "@/types/error";

interface PacientDisplayProps {
  dataArray: Array<Pacient>;
  setDataArray: React.Dispatch<React.SetStateAction<Array<Pacient>>>;
}

interface DeleteButtonProps {
  dataArray: Array<Pacient>;
  setDataArray: React.Dispatch<React.SetStateAction<Array<Pacient>>>;
  id: number;
}

const PatientDisplay = ({ setDataArray }: PacientDisplayProps) => {
  const dataArray = useGetPatients();

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
              <DetailsButton id={id} />
            </TableCell>
            <TableCell className="text-right">
              <MakeAppointmentButton id={id} />
            </TableCell>
            <TableCell className="text-right">
              <DeleteButton
                id={id}
                dataArray={dataArray}
                setDataArray={setDataArray}
              />
            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </TableStyled>
  );
};

const DetailsButton = ({ id }: { id: number }) => {
  const navigate = useNavigate();

  const getDetailsPage = (id: number) => {
    navigate(`${PATIENT_DETAILS_ROUTE}/${id}`);
  };

  return (
    <Button
      variant="outline"
      size="sm"
      className="rounded-full w-8 h-8"
      onClick={() => getDetailsPage(id)}
    >
      <Eye />
    </Button>
  );
};

const MakeAppointmentButton = ({ id }: { id: number }) => {
  const navigate = useNavigate();

  const getDetailsPage = (id: number) => {
    navigate(`${APPOINTMENT_ROUTE}/${id}`);
  };

  return (
    <Button
      variant="outline"
      className="rounded-full"
      onClick={() => getDetailsPage(id)}
    >
      Umów wizytę
    </Button>
  );
};

const DeleteButton = ({ id, dataArray, setDataArray }: DeleteButtonProps) => {
  const deletePacient = async (id: number) => {
    try {
      await callDeletePatient(id);

      setDataArray(
        dataArray.filter((pacient: { id: number }) => pacient.id !== id)
      );
      toast.success("Pacjent został usunięty");
    } catch (err) {
      toast.error(
        (err as ErrorType).response?.data || "Nie można było usunąć gabinetu"
      );
    }
  };
  return (
    <Button
      variant="outline"
      size="sm"
      className="rounded-full w-8 h-8"
      onClick={() => deletePacient(id)}
    >
      <X />
    </Button>
  );
};

export default PatientDisplay;
