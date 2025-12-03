import React from "react";
import RouteButton from "./RouteButton";
import { ADMIN_PANEL_ROUTE, DETAILS_ROUTE, HOME_ROUTE } from "@/text/navbar";

const Navbar = () => {
  return (
    <div>
      <RouteButton path={HOME_ROUTE} label="Home" />
      <RouteButton path={ADMIN_PANEL_ROUTE} label="Admin Panel" />
      <RouteButton path={DETAILS_ROUTE} label="Details" />
    </div>
  );
};

export default Navbar;
