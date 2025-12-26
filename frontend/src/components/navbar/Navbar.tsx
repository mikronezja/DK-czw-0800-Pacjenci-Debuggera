import React, { useEffect } from "react";
import { ROUTES } from "@/constants/routes";
import { DarkModeSwitch } from "./DarkModeSwitch";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { useNavigate } from "react-router-dom";
import styled from "styled-components";
import { usePage } from "../PageProvider";
import { Button } from "../ui/button";
import { ArrowLeft } from "lucide-react";
import { useLocation } from "react-router-dom";

const NavBarStyled = styled.div`
  display: flex;
  padding: 16px;
  flex-direction: row;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
  border-bottom: 1px solid #e0e0e0;
`;

const LeftBarStyled = styled.div`
  display: flex;
  flex-direction: row;
  gap: 16px;
`;

const Navbar = () => {
  const { page, setPage } = usePage();
  const navigate = useNavigate();
  const { pathname } = useLocation();

  useEffect(() => {
    const matchingKey = Object.keys(ROUTES).find(
      (key) => ROUTES[key as keyof typeof ROUTES] === pathname
    );
    if (matchingKey) {
      setPage(matchingKey);
    }
  }, [pathname]);

  return (
    <NavBarStyled>
      <Button
        variant="ghost"
        size="icon"
        onClick={() => {
          navigate(-1);
        }}
      >
        <ArrowLeft className="h-4 w-4" />
      </Button>
      <LeftBarStyled>
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
      </LeftBarStyled>
    </NavBarStyled>
  );
};

export default Navbar;
