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
import { callGetDoctorById, callGetDoctorShifts } from "@/api/doctor_calls";
import { SPECIALIZATIONS } from "@/constants/specializations";
import {
  Divider,
  SectionTitle,
  TableDetailsStyled,
} from "@/styles/styledcomponent";
import { WEEKDAYS } from "@/constants/weekdays";
import {
  DOCTOR_DISPLAY_VALS,
  SHIFT_DISPLAY_VALS,
} from "@/constants/displaying";
import { Button } from "@/components/ui/button";
import { Trash } from "lucide-react";

type ShiftType = {
  office: { id: number; roomNumber: number };
  dayOfWeek: string;
  startTime: string;
  endTime: string;
};

const deleteShift = async (shiftId: number) => {
  // Implement shift deletion logic here
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
    shifts: [],
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
    <div>
      <TableDetailsStyled>
        <TableHeader>
          <TableRow>
            {DOCTOR_DISPLAY_VALS.map((head) => (
              <TableHead key={head}>{head}</TableHead>
            ))}
          </TableRow>
        </TableHeader>
        <TableBody>
          <TableRow>
            <DisplayDoctorInfo doctor={doctor} />
          </TableRow>
        </TableBody>
      </TableDetailsStyled>
      <Divider />
      <DisplayDoctorShifts doctorId={doctor.id} />
    </div>
  );
};

const getDoctorShifts = (doctorId: number) => {
  const [shifts, setShifts] = useState<ShiftType[]>([]);

  useEffect(() => {
    if (doctorId === 0) return;

    const getShifts = async () => {
      try {
        const response = await callGetDoctorShifts(Number(doctorId));
        setShifts(response.data);
      } catch (err) {
        console.error("Error getting shifts:", err);
      }
    };

    getShifts();
  }, [doctorId]);

  return shifts;
};

const DisplayDoctorInfo = ({ doctor }: { doctor: Doctor }) => {
  return (
    <>
      <TableCell>{doctor.name}</TableCell>
      <TableCell>{doctor.surname}</TableCell>
      <TableCell>{SPECIALIZATIONS[doctor.specialization]}</TableCell>
      <TableCell>{doctor.address}</TableCell>
    </>
  );
};

const DisplayDoctorShifts = ({ doctorId }: { doctorId: number }) => {
  const shifts = getDoctorShifts(doctorId);

  return (
    <>
      {shifts.length === 0 ? (
        <SectionTitle>Brak dyżurów</SectionTitle>
      ) : (
        <TableDetailsStyled>
          <TableHeader>
            <TableRow>
              {SHIFT_DISPLAY_VALS.map((head) => (
                <TableHead key={head}>{head}</TableHead>
              ))}
            </TableRow>
            <TableRow />
          </TableHeader>
          <TableBody>
            <ShiftDisplay shifts={shifts} />
          </TableBody>
        </TableDetailsStyled>
      )}
    </>
  );
};

const ShiftDisplay = ({ shifts }: { shifts: ShiftType[] }) => {
  return shifts.map((shift) => (
    <TableRow key={`${shift.dayOfWeek}-${shift.startTime}`}>
      <TableCell>{WEEKDAYS[shift.dayOfWeek]}</TableCell>
      <TableCell>{shift.startTime}</TableCell>
      <TableCell>{shift.endTime}</TableCell>
      <TableCell>{shift.office.roomNumber}</TableCell>
      <TableCell>
        <Button variant="outline" onClick={() => {}}>
          <Trash />
        </Button>
      </TableCell>
    </TableRow>
  ));
};

export default DoctorDetailsPage;
