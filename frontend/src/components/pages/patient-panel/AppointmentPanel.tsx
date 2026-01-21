import { Field, FieldLabel } from "@/components/ui/field";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectValue,
} from "@/components/ui/select";
import { SPECIALIZATIONS } from "@/constants/specializations";
import { useGetPatientById } from "@/hooks/useGetPatientById";
import {
  FormBorder,
  Layout,
  SelectTriggerStyled,
} from "@/styles/styledcomponent";
import type { Appointment } from "@/types/appointment";
import { ChevronDown } from "lucide-react";
import { useState } from "react";
import { useParams } from "react-router-dom";

const AppointmentPanel = () => {
  const { idValue } = useParams();
  const pacient = useGetPatientById(Number(idValue)) || {
    name: "",
    surname: "",
  };
  const [specialization, setSpecializations] = useState<string | null>(null);

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
                {Object.entries(SPECIALIZATIONS).map(
                  ([spec, displayName], key) => (
                    <SelectItem key={key} value={spec}>
                      {displayName}
                    </SelectItem>
                  )
                )}
              </SelectContent>
            </Select>
          </Field>
        </FormBorder>
      </Layout>
    </>
  );
};

export default AppointmentPanel;
