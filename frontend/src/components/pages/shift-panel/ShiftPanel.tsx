import type { Shift } from "@/types/shifts";
import axios from "axios";
import React, { useEffect, useState } from "react";

const shifts = () => {
  const [addOfficeOpen, setAddOfficeOpen] = useState(false);
  const [dataArray, setDataArray] = useState<Shift[]>([]);
  useEffect(() => {
    axios.get("http://localhost:8080/shifts").then((response) => {
      console.log("shifts fetched:", response.data);
      setDataArray(response.data);
    });
  }, []);
  return <div>shifts</div>;
};

export default shifts;
