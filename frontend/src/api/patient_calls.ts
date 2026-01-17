import axios from "axios";
import { PATIENT_ENDPOINT } from "./endpoints";

interface dataType {
  name: string;
  surname: string;
}

export const callGetPatients = async () => {
  return axios.get(PATIENT_ENDPOINT);
};

export const callGetPatientById = async (id: number) => {
  return axios.get([PATIENT_ENDPOINT, id].join("/"));
};

export const callDeletePatient = async (id: number) => {
  return axios.delete([PATIENT_ENDPOINT, id].join("/"));
};

export const callAddPatient = async (data: dataType) => {
  return axios.post(PATIENT_ENDPOINT + "/add", data); // to be deleted later
};
