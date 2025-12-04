import React from "react";
import { useNavigate } from "react-router-dom";

interface RouteButtonProps {
  path: string;
  label: string;
}
const RouteButton = ({ path, label }: RouteButtonProps) => {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate(path);
  };

  return <button onClick={handleClick}>{label}</button>;
};

export default RouteButton;
