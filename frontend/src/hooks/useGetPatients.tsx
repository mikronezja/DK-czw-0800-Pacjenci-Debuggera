import { callGetPatients } from "@/api/patient_calls";
import type { Pacient } from "@/types/pacient";
import { useEffect, useState } from "react";

export const useGetPatients = () => {
  const [patients, setPatients] = useState<Pacient[]>([]);

  useEffect(() => {
    const fetchPatients = async () => {
      try {
        const response = await callGetPatients();

        setPatients(response.data);
      } catch (err) {
        console.log(err);
      }
    };

    fetchPatients();
  }, []);

  return patients;
};
