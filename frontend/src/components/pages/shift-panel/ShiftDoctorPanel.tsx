import { callGetDoctorById } from "@/api/doctor_calls";
import { callGetOffices } from "@/api/office_calls";
import { callAddShift } from "@/api/shift_calls";
import { Button } from "@/components/ui/button";
import { Field, FieldLabel } from "@/components/ui/field";
import { Select, SelectContent, SelectItem } from "@/components/ui/select";
import TimePicker from "@/components/utils/TimePicker";
import { WEEKDAYS } from "@/constants/weekdays";
import {
  FormStyled,
  Layout,
  SelectTriggerStyled,
} from "@/styles/styledcomponent";
import type { ErrorType } from "@/types/error";
import type { Office } from "@/types/office";
import type { DayOfWeekType, Shift } from "@/types/shifts";
import { SelectValue } from "@radix-ui/react-select";
import { ChevronDown } from "lucide-react";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { toast } from "sonner";
import styled from "styled-components";

const FormBorder = styled(FormStyled)`
  margin-top: 10px;
  padding: 40px 30px;
  border-radius: 5px;
  border: 1px solid #e0e0e0;
  min-width: 250px;
  overflow-y: auto;
  min-height: 0;
  max-height: 400px;
`;

const ShiftTime = styled.div`
  display: flex;
  gap: 15px;
  align-items: center;
`;

const ShiftDoctorPanel = () => {
  const navigate = useNavigate();
  const { idValue } = useParams();
  const [doctor, setDoctor] = useState({
    name: "",
    surname: "",
  });
  const [offices, setOffices] = useState<Office[]>([]);
  const [data, setData] = useState<Shift>({
    doctorId: Number(idValue),
    officeId: undefined,
    dayOfWeek: "MONDAY",
    startTime: "01:00",
    endTime: "02:00",
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
      console.log("udało sie dodać zmiane");
      toast.success("Zmiana została dodana");
    } catch (err: unknown) {
      toast.error(
        (err as ErrorType).response?.data || "Nie można było dodać zmiany"
      );
    }
  };

  const deleteShift = async () => {
    navigate(-1);
  };

  useEffect(() => {
    fetchDoctor();
    fetchOffices();
  }, []);

  return (
    <Layout>
      <div style={{ marginTop: "10px" }}>
        {doctor.name} {doctor.surname}
      </div>
      <FormBorder>
        <Field>
          <FieldLabel>Czas zmiany</FieldLabel>
          <ShiftTime>
            <TimePicker
              placeholder="01:00"
              time={data.startTime}
              onChange={(newTime) => {
                setData((prev) => ({
                  ...prev,
                  startTime: newTime,
                }));
              }}
            />
            -
            <TimePicker
              placeholder="02:00"
              time={data.endTime}
              onChange={(newTime) => {
                setData((prev) => ({
                  ...prev,
                  endTime: newTime,
                }));
              }}
            />
          </ShiftTime>
        </Field>
        <Field>
          <FieldLabel>Dzień tygodnia</FieldLabel>
          <Select
            value={data.dayOfWeek}
            onValueChange={(value) => {
              const day = value as DayOfWeekType;
              setData({ ...data, dayOfWeek: day });
            }}
          >
            <SelectTriggerStyled className="w-[180px]">
              <SelectValue placeholder="Monday" />
              <ChevronDown />
            </SelectTriggerStyled>
            <SelectContent>
              {Object.entries(WEEKDAYS).map(([value, displayName], key) => (
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
            value={data.officeId?.toString()}
            onValueChange={(val) => {
              setData({ ...data, officeId: Number(val) });
            }}
          >
            <SelectTriggerStyled className="w-[180px]">
              <SelectValue placeholder="Numer pokoju" />
              <ChevronDown />
            </SelectTriggerStyled>
            <SelectContent>
              {offices.map(({ roomNumber, id }, key) => (
                <SelectItem value={id.toString()} key={key}>
                  {roomNumber.toString()}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </Field>
        <Button
          variant="outline"
          size="sm"
          onClick={(e) => {
            e.preventDefault();
            addShift();
          }}
        >
          Zapisz
        </Button>
        <Button
          variant="outline"
          size="sm"
          onClick={(e) => {
            e.preventDefault();
            deleteShift();
          }}
        >
          Anuluj
        </Button>
      </FormBorder>
    </Layout>
  );
};

export default ShiftDoctorPanel;
