import React, { useContext } from "react";
import { createContext, useState } from "react";
import type { ReactNode } from "react";

interface PageContextValue {
  page: string;
  setPage: (page: string) => void;
}

const PageContext = createContext<PageContextValue | undefined>(undefined);

export const PageProvider = ({ children }: { children: ReactNode }) => {
  const [page, setPage] = useState<string>("Home");
  return (
    <PageContext.Provider value={{ page, setPage }}>
      {children}
    </PageContext.Provider>
  );
};

export function usePage() {
  const ctx = useContext(PageContext);
  if (!ctx) throw new Error("usePage must be used within a PageProvider");
  return ctx;
}
