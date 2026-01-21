import { callGetPatientById } from "@/api/patient_calls";
import type { Pacient } from "@/types/pacient";
import { useEffect, useState } from "react";

export const useGetPatientById = (id: number) => {
  const [pacient, setPatient] = useState<Pacient>();

  useEffect(() => {
    const fetchPatient = async () => {
      try {
        const response = await callGetPatientById(id);

        setPatient(response.data);
      } catch (err) {
        console.log(err);
      }
    };

    fetchPatient();
  }, []);

  return pacient;
};
