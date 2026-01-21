import { callGetDoctors } from "@/api/doctor_calls";
import type { Doctor } from "@/types/doctor";
import { useEffect, useState } from "react";

export const useGetDoctors = () => {
  const [doctors, setDoctor] = useState<Doctor[]>([]);

  useEffect(() => {
    const fetchDoctors = async () => {
      try {
        const response = await callGetDoctors();

        setDoctor(response.data);
      } catch (err) {
        console.log(err);
      }
    };

    fetchDoctors();
  }, []);

  return doctors;
};
