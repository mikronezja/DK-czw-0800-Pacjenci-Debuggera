export interface Shift {
  id: number;
  doctor: {
    id: number;
    name: string;
    surname: string;
    specialization: string;
    address: string;
  };
  office: {
    id: number;
    roomNumber: number;
  };
  dayOfWeek:
    | "MONDAY"
    | "TUESDAY"
    | "WEDNESDAY"
    | "THURSDAY"
    | "FRIDAY"
    | "SATURDAY"
    | "SUNDAY";
  startTime: {
    hour: number;
    minute: number;
    second: number;
    nano: number;
  };
  endTime: {
    hour: number;
    minute: number;
    second: number;
    nano: number;
  };
}
