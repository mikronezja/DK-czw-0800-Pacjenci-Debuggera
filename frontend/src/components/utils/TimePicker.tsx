import React from "react";
import { Field } from "../ui/field";
import { ChevronDown, ChevronUp } from "lucide-react";
import { Input } from "../ui/input";
import styled from "styled-components";

interface TimePickerProps {
  placeholder: string;
  time: number;
  setTime: React.Dispatch<React.SetStateAction<number>>;
  onChange: () => void;
}

const TimePickerStyled = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
`;

const TimePicker = ({
  placeholder,
  time,
  setTime,
  onChange,
}: TimePickerProps) => {
  return (
    <TimePickerStyled>
      <ChevronUp
        onClick={() => {
          setTime(time! + 1 <= 23 ? time! + 1 : 0);
          onChange();
        }}
      />
      <Field>
        <Input placeholder={placeholder} disabled value={time} />
      </Field>
      <ChevronDown
        onClick={() => {
          setTime(time! - 1 >= 0 ? time! - 1 : 23);
          onChange();
        }}
      />
    </TimePickerStyled>
  );
};

export default TimePicker;
