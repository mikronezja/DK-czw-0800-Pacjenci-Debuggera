import type { WEEK_DAYS } from "@/constants/weekdays";

export type DayOfWeekType = keyof typeof WEEK_DAYS;

export interface Shift {
  doctorId: number | undefined;
  officeId: number | undefined;
  dayOfWeek: DayOfWeekType;
  startTime: {
    hour: number;
    // minute: number;
    // second: number;
    // nano: number;
  };
  endTime: {
    hour: number;
    // minute: number;
    // second: number;
    // nano: number;
  };
}
