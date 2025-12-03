import React, { useEffect, useState } from "react";
import axios from "axios";

const DoctorDisplay = () => {
  const [dataArray, setDataArray] = useState([]);
  const deleteDoctor = () => {};

  const fetchData = () => {
    axios
      .get("http://localhost:8080/doctors")
      .then((res) => {
        console.log("worked!");
        setDataArray(res.data);
      })
      .catch((err) => console.error(err));
  };

  useEffect(() => {
    fetchData();
  }, []);

  // map -> doctors for each doctor i can display
  return (
    <div>
      {dataArray.map(({ name, surname, specialization }, index) => (
        <li key={index}>
          {name} {surname} {specialization}
        </li>
      ))}
    </div>
  );
};

export default DoctorDisplay;
