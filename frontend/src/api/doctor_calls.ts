import axios from "axios";
import { DOCTOR_ENDPOINT, SHIFT_ENDPOINT } from "./endpoints";

export const callGetDoctors = async () => {
    return axios.get(DOCTOR_ENDPOINT);
} 

export const callGetDoctorById = async (id : number) => {
    return axios.get([DOCTOR_ENDPOINT,id].join("/"));
} 

export const callDeleteDoctor = async (id : number) => {
    return axios.delete([DOCTOR_ENDPOINT,id].join("/"));
} 

export const callAddDoctor = async () => {
    return axios.post(DOCTOR_ENDPOINT + "/add"); // to be deleted later
} 

export const callGetDoctorShifts = async (id : number) => {
    return axios.get([DOCTOR_ENDPOINT,id,SHIFT_ENDPOINT].join("/"));
} 