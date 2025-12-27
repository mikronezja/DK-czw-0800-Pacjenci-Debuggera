import { callGetDoctorById } from "@/api/doctor_calls";
import { callAddShift } from "@/api/shift_calls";
import { Select, SelectContent, SelectItem } from "@/components/ui/select";
import { WEEK_DAYS } from "@/constants/weekdays";
import { SelectTriggerStyled } from "@/styles/styledcomponent";
import type { DayOfWeekType, Shift } from "@/types/shifts";
import { SelectTrigger, SelectValue } from "@radix-ui/react-select";
import {
  ArrowDown,
  ArrowDown01,
  ArrowDownIcon,
  ChevronDown,
} from "lucide-react";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

const ShiftDoctorPanel = () => {
  const { idValue } = useParams(); // doctor id
  const [doctor, setDoctor] = useState({
    name: "",
    surname: "",
  });
  const [offices, setOffices] = useState({
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
  }, []);

  return (
    <>
      <div>
        {doctor.name} {doctor.surname}
      </div>
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
    </>
  );
};

export default ShiftDoctorPanel;
