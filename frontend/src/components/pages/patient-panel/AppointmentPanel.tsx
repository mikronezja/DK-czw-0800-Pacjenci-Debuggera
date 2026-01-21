import { Field, FieldLabel } from "@/components/ui/field";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectValue,
} from "@/components/ui/select";
import { useGetDoctors } from "@/hooks/useGetDoctors";
import { useGetPatientById } from "@/hooks/useGetPatientById";
import {
  FormBorder,
  FormStyled,
  Layout,
  SelectTriggerStyled,
} from "@/styles/styledcomponent";
import type { Appointment } from "@/types/appointment";
import { ChevronDown } from "lucide-react";
import { useState } from "react";
import { useParams } from "react-router-dom";

const AppointmentPanel = () => {
  const { idValue } = useParams();
  const doctors = useGetDoctors();
  const pacient = useGetPatientById(Number(idValue)) || {
    name: "",
    surname: "",
  };

  const [data, setData] = useState<Appointment>({
    doctorId: Number(idValue),
    pacientId: undefined,
    date: "",
    startTime: "",
    endTime: "",
  });

  return (
    <>
      <Layout>
        <div style={{ marginTop: "10px" }}>
          {pacient.name} {pacient.surname}
        </div>

        <FormBorder>
          <Field>
            <FieldLabel>Lekarz</FieldLabel>
            <Select
              onValueChange={(val) => {
                setData({ ...data, doctorId: Number(val) });
              }}
            >
              <SelectTriggerStyled className="w-[180px]">
                <SelectValue placeholder="Lekarz..." />
                <ChevronDown />
              </SelectTriggerStyled>
              <SelectContent>
                {doctors.map(({ id, name, surname }, key) => (
                  <SelectItem key={key} value={id.toString()}>
                    {name} {surname}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </Field>
        </FormBorder>
      </Layout>
    </>
  );
};

export default AppointmentPanel;
