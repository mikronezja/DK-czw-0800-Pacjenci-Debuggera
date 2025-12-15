import React from "react";

interface ActionButtonProps {
  handleClick: (e: React.SyntheticEvent) => void;
  label: string;
  type?: "button" | "submit" | "reset" | undefined;
}

const ActionButton = ({
  handleClick,
  label,
  type = "button",
}: ActionButtonProps) => {
  return (
    <button type={type} onClick={handleClick}>
      {label}
    </button>
  );
};

export default ActionButton;
