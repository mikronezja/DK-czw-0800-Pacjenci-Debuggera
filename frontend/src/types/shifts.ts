import type { WEEKDAYS } from "@/constants/weekdays";

export type DayOfWeekType = keyof typeof WEEKDAYS;

export interface Shift {
  doctorId: number | undefined;
  officeId: number | undefined;
  dayOfWeek: DayOfWeekType;
  startTime: string;
  endTime: string;
}
