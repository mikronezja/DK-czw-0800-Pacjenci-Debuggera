import { Field } from "../ui/field";
import { Input } from "../ui/input";
import styled from "styled-components";

interface TimePickerProps {
  time: string;
  placeholder: string;
  onChange: (newTime: string) => void;
}

const TimePickerStyled = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
`;

const TimePicker = ({ placeholder, time, onChange }: TimePickerProps) => {
  return (
    <TimePickerStyled>
      <Field>
        <Input
          placeholder={placeholder}
          value={time}
          onChange={(e) => onChange(e.target.value)}
        />
      </Field>
    </TimePickerStyled>
  );
};

export default TimePicker;
