import { callGetDoctorById } from "@/api/doctor_calls";
import { callGetOffices } from "@/api/office_calls";
import { callAddShift } from "@/api/shift_calls";
import { Field, FieldLabel } from "@/components/ui/field";
import { Select, SelectContent, SelectItem } from "@/components/ui/select";
import { WEEK_DAYS } from "@/constants/weekdays";
import { FormStyled, SelectTriggerStyled } from "@/styles/styledcomponent";
import type { Office } from "@/types/office";
import type { DayOfWeekType, Shift } from "@/types/shifts";
import { SelectValue } from "@radix-ui/react-select";
import { ChevronDown } from "lucide-react";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import styled from "styled-components";

const FormBorder = styled(FormStyled)`
  margin-top: 10px;
  padding: 40px 30px;
  border-radius: 5px;
  border: 1px solid #e0e0e0;
  min-width: 250px;
  overflow-y: auto;
  gap: 25px;
  max-height: 400px;
`;

const ShiftDoctorPanel = () => {
  const { idValue } = useParams(); // doctor id
  const [doctor, setDoctor] = useState({
    name: "",
    surname: "",
  });
  const [offices, setOffices] = useState<Office[]>([]);
  const [office, setOffice] = useState({
    roomNumber: -1,
  });
  const [weekDay, setWeekDay] = useState<DayOfWeekType>("MONDAY");
  const [data, setData] = useState<Shift>({
    doctorId: Number(idValue),
    officeId: undefined,
    dayOfWeek: "MONDAY",
    startTime: {
      hour: 1,
    },
    endTime: {
      hour: 2,
    },
  });

  const fetchDoctor = async () => {
    try {
      const response = await callGetDoctorById(Number(idValue));

      setDoctor({ name: response.data.name, surname: response.data.surname });
    } catch (err) {
      console.error("Error fetching doctors:", err);
    }
  };

  const fetchOffices = async () => {
    try {
      const response = await callGetOffices();

      setOffices(response.data);
    } catch (err) {
      console.error("Error fetching doctors:", err);
    }
  };

  const addShift = async () => {
    try {
      await callAddShift(data);
    } catch (err: any) {
      if (err.response.status == 400) {
        // cannot add shift
      }
      console.log(err);
    }
  };

  useEffect(() => {
    fetchDoctor();
    fetchOffices();
  }, []);

  return (
    <FormStyled>
      <div style={{ marginTop: "10px" }}>
        {doctor.name} {doctor.surname}
      </div>
      <FormBorder>
        <Field>
          <FieldLabel>Rozpoczęcie zmiany</FieldLabel>
          <Select>
            <SelectTriggerStyled className="w-[180px]">
              <SelectValue placeholder="Godzina" />
              <ChevronDown />
            </SelectTriggerStyled>
          </Select>
        </Field>
        <Field>
          <FieldLabel>Zakończenie zmiany</FieldLabel>
          <Select>
            <SelectTriggerStyled className="w-[180px]">
              <SelectValue placeholder="Godzina" />
              <ChevronDown />
            </SelectTriggerStyled>
          </Select>
        </Field>
        <Field>
          <FieldLabel>Dzień tygodnia</FieldLabel>
          <Select
            value={weekDay}
            onValueChange={(value) => {
              const day = value as DayOfWeekType;
              setWeekDay(day);
              setData({ ...data, dayOfWeek: day });
            }}
          >
            <SelectTriggerStyled className="w-[180px]">
              <SelectValue placeholder="Monday" />
              <ChevronDown />
            </SelectTriggerStyled>
            <SelectContent>
              {Object.entries(WEEK_DAYS).map(([value, displayName], key) => (
                <SelectItem key={key} value={value}>
                  {displayName}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </Field>
        <Field>
          <FieldLabel>Gabinet</FieldLabel>
          <Select
            value={office.roomNumber.toString()}
            onValueChange={(val) => {
              setOffice({ roomNumber: Number(val) });
            }}
          >
            <SelectTriggerStyled className="w-[180px]">
              <SelectValue placeholder="Numer pokoju" />
              <ChevronDown />
            </SelectTriggerStyled>
            <SelectContent>
              {offices.map(({ roomNumber }, key) => (
                <SelectItem value={roomNumber.toString()} key={key}>
                  {roomNumber.toString()}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </Field>
      </FormBorder>
    </FormStyled>
  );
};

export default ShiftDoctorPanel;
