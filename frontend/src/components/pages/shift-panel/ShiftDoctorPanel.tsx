import type { Shift } from "@/types/shifts";
import { useEffect, useState } from "react";

const ShiftDoctorPanel = () => {
  const [addOfficeOpen, setAddOfficeOpen] = useState(false);
  const [dataArray, setDataArray] = useState<Shift[]>([]);

  // const fetchShifts = async () => {
  //   try
  //   {
  //     await callGet
  //   }
  //   catch (err)
  //   {
  //     console.log(err);
  //   }
  // }

  useEffect(() => {}, []);
  return <div>shifts</div>;
};

export default ShiftDoctorPanel;
