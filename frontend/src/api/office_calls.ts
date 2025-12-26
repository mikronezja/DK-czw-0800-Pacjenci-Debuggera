import axios from "axios";
import { OFFICE_ENDPOINT, SHIFT_ENDPOINT } from "./endpoints";

export const callGetOffices = async () => {
    return axios.get(OFFICE_ENDPOINT);
} 

export const callGetOfficeShifts = async (id : number) => {
    return axios.get([OFFICE_ENDPOINT, id, SHIFT_ENDPOINT].join("/"));
} 

export const callDeleteOffice = async (id : number) => {
    return axios.delete([OFFICE_ENDPOINT, id].join("/"));
} 

export const callAddOffice = async () => {
    return axios.post(OFFICE_ENDPOINT + "/add"); // to be deleted later
} 