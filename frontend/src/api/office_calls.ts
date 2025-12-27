import axios from "axios";
import { OFFICE_ENDPOINT, SHIFT_ENDPOINT } from "./endpoints";
import type { Shift } from "@/types/shifts";

interface dataType {
  roomNumber: number;
  shifts: Shift[];
}

export const callGetOffices = async () => {
  return axios.get(OFFICE_ENDPOINT);
};

export const callGetOfficeShifts = async (id: number) => {
  return axios.get([OFFICE_ENDPOINT, id, SHIFT_ENDPOINT].join("/"));
};

export const callDeleteOffice = async (id: number) => {
  return axios.delete([OFFICE_ENDPOINT, id].join("/"));
};

export const callAddOffice = async (data: dataType) => {
  return axios.post(OFFICE_ENDPOINT + "/add", data); // to be deleted later
};
