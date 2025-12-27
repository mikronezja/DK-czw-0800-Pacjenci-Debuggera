import axios from "axios";
import { SHIFT_ENDPOINT } from "./endpoints";
import type { Shift } from "@/types/shifts";

export const callPutShift = async (id: number) => {
  return axios.get([SHIFT_ENDPOINT, id].join("/"));
};

export const callDeleteShift = async (id: number) => {
  return axios.delete([SHIFT_ENDPOINT, id].join("/"));
};

export const callAddShift = async (data: Shift) => {
  return axios.post(SHIFT_ENDPOINT + "/add", data); // to be deleted later
};
