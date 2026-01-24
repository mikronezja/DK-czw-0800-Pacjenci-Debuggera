import { callGetDoctorById } from "@/api/doctor_calls";
import type { Doctor } from "@/types/doctor";
import { useEffect, useState } from "react";

export const useGetDoctorById = (id: number) => {
  const [doctor, setDoctor] = useState<Doctor>();

  useEffect(() => {
    const fetchDoctor = async () => {
      try {
        const response = await callGetDoctorById(id);

        setDoctor(response.data);
      } catch (err) {}
    };

    fetchDoctor();
  }, []);

  return doctor;
};
