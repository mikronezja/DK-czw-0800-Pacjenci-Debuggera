import { callAddAppointment } from "@/api/appointment_calls";
import { Button } from "@/components/ui/button";
import { Field, FieldContent, FieldLabel } from "@/components/ui/field";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectValue,
} from "@/components/ui/select";
import { PATIENT_PANEL_ROUTE } from "@/constants/routes";
import { SelectTriggerStyled } from "@/styles/styledcomponent";
import type {
  AppointmentAvailabilitiesType,
  AppointmentCallProps,
} from "@/types/appointment";
import type { ErrorType } from "@/types/error";
import { ChevronDown } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { toast } from "sonner";
import styled from "styled-components";

interface AvailabilitiesContainerProps {
  availabilities: AppointmentAvailabilitiesType[];
  setData: React.Dispatch<React.SetStateAction<AppointmentCallProps>>;
  data: AppointmentCallProps;
}

interface Props {
  availabilities: AppointmentAvailabilitiesType[];
  setData: React.Dispatch<React.SetStateAction<AppointmentCallProps>>;
  data?: AppointmentCallProps;
}

const OtherPropertiesStyled = styled.div`
  display: flex;
  gap: 20px;
  flex-direction: column;
`;

const EndTimeStyled = styled(Field)`
  display: flex;
  flex-direction: row;
  align-items: center;
`;

const addTime = (time: string, minutesToAdd = 15): string => {
  const [hours, minutes] = time.split(":").map(Number);
  const totalMinutes = hours * 60 + minutes + minutesToAdd;
  const newHours = Math.floor(totalMinutes / 60) % 24;
  const newMinutes = totalMinutes % 60;

  return `${newHours.toString().padStart(2, "0")}:${newMinutes
    .toString()
    .padStart(2, "0")}`;
};

const generateTimeSlots = (startTime: string, endTime: string): string[] => {
  const slots: string[] = [];
  let currentTime = startTime.slice(0, -3);
  while (addTime(currentTime) <= endTime) {
    slots.push(currentTime);
    currentTime = addTime(currentTime);
  }
  return slots;
};

const postAppointment = async (data: AppointmentCallProps) => {
  try {
    const properties = {
      doctorId: data.doctorId!,
      patientId: data.patientId!,
      date: data.date!,
      startTime: data.startTime!,
      endTime: data.endTime!,
    };
    console.log("Posting appointment with data:", properties);
    await callAddAppointment(properties);
    toast.success("Wizyta została zarezerwowana!");
  } catch (err) {
    toast.error((err as ErrorType).response.data);
  }
};

export const AvailabilitiesContainer = ({
  availabilities,
  setData,
  data,
}: AvailabilitiesContainerProps) => {
  const navigate = useNavigate();
  return (
    <OtherPropertiesStyled>
      <DoctorSelect availabilities={availabilities} setData={setData} />
      {data.doctorId && (
        <TimeSlotsSelect
          data={data}
          availabilities={availabilities}
          setData={setData}
        />
      )}
      {data.startTime && (
        <EndTimeStyled>
          <FieldLabel>Czas zakończenia: {data.endTime.slice(0, -3)}</FieldLabel>
        </EndTimeStyled>
      )}
      {data.startTime && (
        <Button
          onClick={() => {
            postAppointment(data);
            navigate(`${PATIENT_PANEL_ROUTE}`);
          }}
        >
          Zarezerwuj wizytę
        </Button>
      )}
    </OtherPropertiesStyled>
  );
};

const DoctorSelect = ({ availabilities, setData }: Props) => {
  return (
    <Field>
      <FieldLabel>Lekarz</FieldLabel>
      <Select
        onValueChange={(val) => {
          console.log("Selected doctorId:", val);
          setData((prev) => ({ ...prev, doctorId: parseInt(val) }));
        }}
      >
        <SelectTriggerStyled className="w-[180px]">
          <SelectValue placeholder="Lekarz..." />
          <ChevronDown />
        </SelectTriggerStyled>
        <SelectContent>
          {availabilities.map(({ doctorId, doctorName }, key) => (
            <SelectItem key={key} value={doctorId.toString()}>
              {doctorName}
            </SelectItem>
          ))}
        </SelectContent>
      </Select>
    </Field>
  );
};

const TimeSlotsSelect = ({ availabilities, setData, data }: Props) => {
  return (
    <Field>
      <FieldLabel>Czas rozpoczęcia</FieldLabel>
      <Select
        onValueChange={(val) => {
          setData((prev) => ({
            ...prev,
            startTime: val + ":00",
            endTime: addTime(val) + ":00",
          }));
        }}
      >
        <SelectTriggerStyled className="w-[180px]">
          <SelectValue />
          <ChevronDown />
        </SelectTriggerStyled>
        <SelectContent>
          {availabilities
            .filter((a) => a.doctorId === data!.doctorId)
            .map(({ timeRanges }, key) =>
              timeRanges.map(({ startTime, endTime }, idx) =>
                generateTimeSlots(startTime, endTime).map((slot, slotIdx) => (
                  <SelectItem key={`${key}-${idx}-${slotIdx}`} value={slot}>
                    {slot}
                  </SelectItem>
                ))
              )
            )}
        </SelectContent>
      </Select>
    </Field>
  );
};
