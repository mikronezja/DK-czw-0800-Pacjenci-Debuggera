import { callAppointmentAvailabilities } from "@/api/appointment_calls";
import { Calendar } from "@/components/ui/calendar";
import { Field, FieldLabel } from "@/components/ui/field";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectValue,
} from "@/components/ui/select";
import { SPECIALIZATIONS } from "@/constants/specializations";
import { useGetPatientById } from "@/hooks/useGetPatientById";
import { Layout, SelectTriggerStyled } from "@/styles/styledcomponent";
import type {
  AppointmentAvailabilitiesType,
  AppointmentCallProps,
} from "@/types/appointment";
import { ChevronDown } from "lucide-react";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { toast } from "sonner";
import styled from "styled-components";
import { AvailabilitiesContainer } from "./AvailabilitiesContainer";

interface CalendarContainerProps {
  data: AppointmentCallProps;
  setData: React.Dispatch<React.SetStateAction<AppointmentCallProps>>;
}

const PropertiesStyled = styled.div`
  display: flex;
  gap: 20px;
  width: 50%;
  margin-top: 20px;
  min-width: 500px;
`;

const CalendarStyled = styled(Calendar)`
  flex: 1;
  max-width: 300px;
`;

const OtherPropertiesStyled = styled.div`
  display: flex;
  flex: 0.8;
  gap: 20px;
  flex-direction: column;
`;

const AppointmentPanel = () => {
  const { idValue } = useParams();
  const pacient = useGetPatientById(Number(idValue)) || {
    name: "",
    surname: "",
  };
  const [specialization, setSpecializations] = useState<string | undefined>(
    undefined
  );
  const [fetched, setFetched] = useState<boolean>(false);
  const [availabilities, setAvailabilities] = useState<
    AppointmentAvailabilitiesType[] | undefined
  >(undefined);
  const [data, setData] = useState<AppointmentCallProps>({
    doctorId: undefined,
    patientId: Number(idValue),
    date: "",
    startTime: "",
    endTime: "",
  });

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await callAppointmentAvailabilities({
          specialization: specialization!,
          date: data.date,
        });
        setAvailabilities(response.data);
      } catch (err: any) {
        console.log();
        console.log({
          specialization: specialization!,
          date: data.date,
        });
        toast.error(
          err.response?.data || "Wystąpił błąd podczas pobierania dostępności"
        );
        setAvailabilities([]);
      }
      setFetched(true);
    };
    if (specialization && data.date) {
      fetchData();
    }
  }, [specialization, data.date]);

  return (
    <Layout>
      <div style={{ marginTop: "10px" }}>
        {pacient.name} {pacient.surname}
      </div>

      <PropertiesStyled>
        <OtherPropertiesStyled>
          <SpecializationSelect setSpecializations={setSpecializations} />
          {fetched &&
            (availabilities && availabilities.length > 0 ? (
              <AvailabilitiesContainer
                availabilities={availabilities}
                setData={setData}
                data={data}
              />
            ) : null)}
        </OtherPropertiesStyled>
        <div style={{ flex: 1 }}>
          <CalendarContainer data={data} setData={setData} />
        </div>
      </PropertiesStyled>
    </Layout>
  );
};

const SpecializationSelect = ({
  setSpecializations,
}: {
  setSpecializations: React.Dispatch<React.SetStateAction<string | undefined>>;
}) => {
  return (
    <Field>
      <FieldLabel>Specjalizacja</FieldLabel>
      <Select
        onValueChange={(val) => {
          setSpecializations(val);
        }}
      >
        <SelectTriggerStyled className="w-[180px]">
          <SelectValue placeholder="Specjalizacja..." />
          <ChevronDown />
        </SelectTriggerStyled>
        <SelectContent>
          {Object.entries(SPECIALIZATIONS).map(([spec, displayName], key) => (
            <SelectItem key={key} value={spec}>
              {displayName}
            </SelectItem>
          ))}
        </SelectContent>
      </Select>
    </Field>
  );
};

const CalendarContainer = ({ data, setData }: CalendarContainerProps) => {
  return (
    <Field>
      <FieldLabel>Data</FieldLabel>
      <CalendarStyled
        mode="single"
        selected={data.date ? new Date(data.date) : undefined}
        onSelect={(selectedDate) => {
          if (selectedDate) {
            const year = selectedDate.getFullYear();
            const month = String(selectedDate.getMonth() + 1).padStart(2, "0");
            const day = String(selectedDate.getDate()).padStart(2, "0");
            const dateString = `${year}-${month}-${day}`;
            setData({
              ...data,
              date: dateString,
            });
          }
        }}
        className="rounded-md border shadow-sm"
        captionLayout="dropdown"
      />
    </Field>
  );
};

export default AppointmentPanel;
