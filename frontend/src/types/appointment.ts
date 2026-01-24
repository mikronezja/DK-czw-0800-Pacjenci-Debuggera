export interface Appointment {
  id: number;
  doctorId: number | undefined;
  patientId: number | undefined;
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

export type AppointmentCallProps = {
  doctorId: number | undefined;
  patientId: number | undefined;
  date: string;
  startTime: string;
  endTime: string;
};
