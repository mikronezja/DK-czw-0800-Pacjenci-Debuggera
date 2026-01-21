export interface Appointment {
  doctorId: number | undefined;
  pacientId: number | undefined;
  date: string;
  startTime: string;
  endTime: string;
}

export type TimeSlotsType = {
  startTime: string;
  endTime: string;
};

export type AppointmentAvailabilitiesType = {
  doctorId: number;
  doctorName: string;
  timeRanges: Array<TimeSlotsType>;
};
