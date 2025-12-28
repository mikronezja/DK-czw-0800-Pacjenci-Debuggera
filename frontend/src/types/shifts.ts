import type { WEEK_DAYS } from "@/constants/weekdays";

export type DayOfWeekType = keyof typeof WEEK_DAYS;

export interface Shift {
  doctorId: number | undefined;
  officeId: number | undefined;
  dayOfWeek: DayOfWeekType;
  startTime: string;
  endTime: string;
}
