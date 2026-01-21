import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import {
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import {
  callGetPatientAppointments,
  callGetPatientById,
} from "@/api/patient_calls";
import { TableDetailsStyled } from "@/styles/styledcomponent";
import { Button } from "@/components/ui/button";
import { toast } from "sonner";
import { callDeleteAppointments } from "@/api/appointment_calls";
import type { Appointment } from "@/types/appointment";
import { Trash } from "lucide-react";
import type { Doctor } from "@/types/doctor";
import { SPECIALIZATIONS } from "@/constants/specializations";

type PacientAppointment = {
  doctor: Doctor;
  id: number;
  date: string;
  startTime: string;
  endTime: string;
};

const PatientDetailsPage = () => {
  const { idValue } = useParams();
  const [patient, setPatient] = useState({
    name: "",
    surname: "",
    pesel: "",
    address: "",
  });

  const fetchPatients = async () => {
    try {
      const response = await callGetPatientById(Number(idValue));

      setPatient(response.data);
    } catch (err) {
      console.log(err);
    }
  };

  useEffect(() => {
    fetchPatients();
  }, []);
  return (
    <>
      <TableDetailsStyled>
        <div>
          <TableHeader>
            <TableRow>
              <TableHead>Imię</TableHead>
              <TableHead className="w-[100px]">Nazwisko</TableHead>
              <TableHead>PESEL</TableHead>
              <TableHead>Adres</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            <TableRow>
              <TableCell>{patient.name}</TableCell>
              <TableCell>{patient.surname}</TableCell>
              <TableCell>{patient.pesel}</TableCell>
              <TableCell>{patient.address}</TableCell>
            </TableRow>
          </TableBody>
        </div>
      </TableDetailsStyled>
      <DisplayAppointmentInfo patientId={Number(idValue)} />
    </>
  );
};

const usePatientAppointments = (patientId: number) => {
  const [appointments, setAppointments] = useState<PacientAppointment[]>([]);
  console.log("Patient ID in hook:", patientId);
  useEffect(() => {
    const fetchAppointments = async () => {
      try {
        const response = await callGetPatientAppointments({ patientId });
        setAppointments(response.data);
      } catch (err) {
        console.log(err);
      }
    };
    fetchAppointments();
  }, [patientId]);
  return [appointments, setAppointments] as const;
};

const deleteAppointment = async (appointmentId: number) => {
  try {
    await callDeleteAppointments(appointmentId);
    toast.success("Wizyta została usunięta");
  } catch (err: any) {
    toast.error(err.response?.data || "Nie można było usunąć wizyty");
  }
};

const DisplayAppointmentInfo = ({ patientId }: { patientId: number }) => {
  const [appointments, setAppointments] = usePatientAppointments(patientId);
  console.log("Appointments:", patientId, appointments);
  return (
    <>
      {appointments.length === 0 ? (
        <div style={{ display: "flex", justifyContent: "center" }}>
          Brak umówionych wizyt
        </div>
      ) : (
        <TableDetailsStyled>
          <div>
            <TableHeader>
              <TableRow>
                <TableHead>Lekarz</TableHead>
                <TableHead>Specjalizacja</TableHead>
                <TableHead>Data</TableHead>
                <TableHead>Czas rozpoczęcia</TableHead>
                <TableHead>Czas zakończenia</TableHead>
                <TableHead />
              </TableRow>
            </TableHeader>
            <TableBody>
              {appointments.map((appointment) => (
                <TableRow key={appointment.id}>
                  <TableCell>
                    {appointment.doctor.name} {appointment.doctor.surname}
                  </TableCell>
                  <TableCell>
                    {SPECIALIZATIONS[appointment.doctor.specialization]}
                  </TableCell>
                  <TableCell>{appointment.date}</TableCell>
                  <TableCell>{appointment.startTime}</TableCell>
                  <TableCell>{appointment.endTime}</TableCell>
                  <TableCell>
                    <Button
                      variant="outline"
                      onClick={() => {
                        deleteAppointment(appointment.id);
                        setAppointments((prevAppointments) =>
                          prevAppointments.filter(
                            (a) => a.id !== appointment.id
                          )
                        );
                      }}
                    >
                      <Trash />
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </div>
        </TableDetailsStyled>
      )}
    </>
  );
};
export default PatientDetailsPage;
