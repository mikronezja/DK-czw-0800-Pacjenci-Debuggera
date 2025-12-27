import axios from "axios";
import { DOCTOR_ENDPOINT, SHIFT_ENDPOINT } from "./endpoints";

interface dataType {
  name: string;
  surname: string;
  pesel: string;
  specialization: string;
  address: string;
}

export const callGetDoctors = async () => {
  return axios.get(DOCTOR_ENDPOINT);
};

export const callGetDoctorById = async (id: number) => {
  return axios.get([DOCTOR_ENDPOINT, id].join("/"));
};

export const callDeleteDoctor = async (id: number) => {
  return axios.delete([DOCTOR_ENDPOINT, id].join("/"));
};

export const callAddDoctor = async (data: dataType) => {
  return axios.post(DOCTOR_ENDPOINT + "/add", data); // to be deleted later
};

export const callGetDoctorShifts = async (id: number) => {
  return axios.get([DOCTOR_ENDPOINT, id, SHIFT_ENDPOINT].join("/"));
};
