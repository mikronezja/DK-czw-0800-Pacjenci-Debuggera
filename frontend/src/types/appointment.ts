export interface Appointment {
  doctorId: number | undefined;
  pacientId: number | undefined;
  date: string;
  startTime: string;
  endTime: string;
}
