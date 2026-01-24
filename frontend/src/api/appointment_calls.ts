import axios from "axios";
import { APPOINTMENT_ENDPOINT, AVAILABILITIES } from "./endpoints";

interface AppointmentAvailabilitiesProps {
  specialization: string;
  date: string;
}

interface AddingAppointmentProps {
  doctorId: number;
  patientId: number;
  date: string;
  startTime: string;
  endTime: string;
}

export const callGetAppointments = async () => {
  return axios.get(APPOINTMENT_ENDPOINT);
};

export const callAppointmentAvailabilities = async (
  data: AppointmentAvailabilitiesProps
) => {
  return axios.post([APPOINTMENT_ENDPOINT, AVAILABILITIES].join("/"), data);
};

export const callAddAppointment = async (data: AddingAppointmentProps) => {
  return axios.post(APPOINTMENT_ENDPOINT + "/add", data); // to be deleted later
};

export const callDeleteAppointments = async (id: number) => {
  return axios.delete(APPOINTMENT_ENDPOINT + "/" + id);
};
