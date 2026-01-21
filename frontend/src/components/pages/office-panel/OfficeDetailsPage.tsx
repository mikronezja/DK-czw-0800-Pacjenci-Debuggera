import { callGetOffices, callGetOfficeShifts } from "@/api/office_calls";
import {
  TableBody,
  TableCell,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { OFFICE_DISPLAY_VALS } from "@/constants/displaying";
import { SPECIALIZATIONS } from "@/constants/specializations";
import { WEEKDAYS } from "@/constants/weekdays";
import { SectionTitle, TableDetailsStyled } from "@/styles/styledcomponent";
import type { Office } from "@/types/office";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

type ShiftType = {
  doctor: { id: number; name: string; surname: string; specialization: string };
  dayOfWeek: string;
  startTime: string;
  endTime: string;
};

const OfficeDetailsPage = () => {
  const { idValue } = useParams();
  const shifts = useShiftDetails(Number(idValue));

  return (
    <div>
      <DisplayOfficeInfo officeId={Number(idValue)} />
      <TableDetailsStyled>
        <TableHeader>
          <TableRow>
            {OFFICE_DISPLAY_VALS.map((val) => (
              <TableCell key={val}>{val}</TableCell>
            ))}
          </TableRow>
        </TableHeader>
        <TableBody>
          <DisplayShiftInfo shifts={shifts} />
        </TableBody>
      </TableDetailsStyled>
    </div>
  );
};

const useOfficeDetails = (idValue: number) => {
  const [offices, setOffices] = useState<Office[]>([]);
  useEffect(() => {
    const fetchOffice = async () => {
      try {
        const response = await callGetOffices();
        setOffices(response.data);
      } catch (err) {
        console.error("Error fetching office details:", err);
      }
    };
    fetchOffice();
  }, []);
  return offices.filter((office: { id: number }) => office.id === idValue);
};

const useShiftDetails = (idValue: number) => {
  const [shifts, setShifts] = useState([]);
  useEffect(() => {
    const fetchOffice = async () => {
      try {
        const response = await callGetOfficeShifts(Number(idValue));
        setShifts(response.data);
      } catch (err) {
        console.error("Error fetching office details:", err);
      }
    };
    fetchOffice();
  }, []);
  return shifts;
};

const DisplayShiftInfo = ({ shifts }: { shifts: ShiftType[] }) => {
  return (
    <>
      {shifts.map(({ doctor, dayOfWeek, startTime, endTime }, index) => (
        <TableRow key={index}>
          <TableCell>{WEEKDAYS[dayOfWeek]}</TableCell>
          <TableCell>{SPECIALIZATIONS[doctor.specialization]}</TableCell>
          <TableCell>{doctor.name}</TableCell>
          <TableCell>{doctor.surname}</TableCell>
          <TableCell>
            {startTime} - {endTime}
          </TableCell>
        </TableRow>
      ))}
    </>
  );
};

const DisplayOfficeInfo = ({ officeId }: { officeId: number }) => {
  const offices = useOfficeDetails(officeId);
  return (
    <SectionTitle>
      <b>
        {offices[0]
          ? `Numer gabinetu: ${offices[0].roomNumber}`
          : "Brak danych"}
      </b>
    </SectionTitle>
  );
};

export default OfficeDetailsPage;
