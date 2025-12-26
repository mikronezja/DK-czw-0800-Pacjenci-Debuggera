import axios from "axios";
import { SHIFT_ENDPOINT } from "./endpoints";

export const callPutShift = async (id : number) => {
    return axios.get([SHIFT_ENDPOINT,id].join("/"));
} 

export const callDeleteShift = async (id : number) => {
    return axios.delete([SHIFT_ENDPOINT,id].join("/"));
} 

export const callAddShift = async () => {
    return axios.post(SHIFT_ENDPOINT + "/add"); // to be deleted later
} 