import React from "react";
import { ROUTES } from "@/text/navbar";
import { DarkModeSwitch } from "./DarkModeSwitch";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { useNavigate } from "react-router-dom";

const Navbar = () => {
  const [page, setPage] = React.useState<string>("Home");
  const navigate = useNavigate();
  return (
    <>
      <Select
        value={page}
        onValueChange={(value) => {
          setPage(value);
          navigate(ROUTES[value as keyof typeof ROUTES]);
        }}
      >
        <SelectTrigger className="w-[180px]">
          <SelectValue placeholder="Theme" />
        </SelectTrigger>
        <SelectContent>
          {Object.keys(ROUTES).map((route) => (
            <SelectItem key={route} value={route}>
              {route}
            </SelectItem>
          ))}
        </SelectContent>
      </Select>
      <DarkModeSwitch />
    </>
  );
};

export default Navbar;
