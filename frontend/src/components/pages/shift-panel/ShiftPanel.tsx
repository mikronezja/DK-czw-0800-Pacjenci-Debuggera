import type { Shift } from "@/types/shifts";
import { useEffect, useState } from "react";

const shifts = () => {
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

  useEffect(() => {
    // axios.get("http://localhost:8080/shifts").then((response) => {
    //   console.log("shifts fetched:", response.data);
    //   setDataArray(response.data);
    // });
  }, []);
  return <div>shifts</div>;
};

export default shifts;
