import React from "react";
import RouteButton from "./RouteButton";
import { ADMIN_PANEL_ROUTE, HOME_ROUTE } from "@/text/navbar";
import { DarkModeSwitch } from "./DarkModeSwitch";

const Navbar = () => {
  return (
    <div>
      <RouteButton path={HOME_ROUTE} label="Home" />
      <RouteButton path={ADMIN_PANEL_ROUTE} label="Admin Panel" />
      <DarkModeSwitch />
    </div>
  );
};

export default Navbar;
