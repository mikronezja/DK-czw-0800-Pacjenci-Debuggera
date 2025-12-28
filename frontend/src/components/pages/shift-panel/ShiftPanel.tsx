import type { Shift } from "@/types/shifts";
import axios from "axios";
import React, { useEffect, useState } from "react";
import {
  Table,
  TableBody,
  TableCaption,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import styled from "styled-components";
import { Button } from "@/components/ui/button";
import { X } from "lucide-react";

const TableStyled = styled(Table)`
  overflow-y: auto;
  height: 100%;
`;

const ShiftPanelContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 30px;
`;

const formatDayOfWeek = (day: string): string => {
  const mapping: Record<string, string> = {
    MONDAY: "Poniedziałek",
    TUESDAY: "Wtorek",
    WEDNESDAY: "Środa",
    THURSDAY: "Czwartek",
    FRIDAY: "Piątek",
    SATURDAY: "Sobota",
    SUNDAY: "Niedziela",
  };
  return mapping[day] || day;
};

const formatTime = (time: { hour: number; minute: number }): string => {
  return `${time.hour.toString().padStart(2, "0")}:${time.minute.toString().padStart(2, "0")}`;
};

const ShiftPanel = () => {
  const [dataArray, setDataArray] = useState<Shift[]>([]);
  const [loading, setLoading] = useState(false);

  const fetchData = () => {
    setLoading(true);
    axios
      .get("http://localhost:8080/shifts", { timeout: 10000 })
      .then((response: any) => {
        if (response.data && Array.isArray(response.data)) {
          setDataArray(response.data);
        } else {
          setDataArray([]);
        }
        setLoading(false);
      })
      .catch((err: any) => {
        console.error("Error fetching shifts:", err);
        setLoading(false);
        if (err.code === 'ECONNABORTED' || err.message?.includes('timeout')) {
          alert("Timeout - serwer nie odpowiada!");
        } else if (err.code === 'ERR_NETWORK' || err.message?.includes('Network Error')) {
          alert("Błąd połączenia z serwerem!");
        } else {
          alert("Błąd podczas pobierania dyżurów!");
        }
        setDataArray([]);
      });
  };

  const deleteShift = (id: number) => {
    if (!window.confirm("Czy na pewno chcesz usunąć ten dyżur?")) {
      return;
    }
    
    axios
      .delete(`http://localhost:8080/shifts/${id}`, { timeout: 10000 })
      .then(() => {
        fetchData();
      })
      .catch((err: any) => {
        console.error("Error deleting shift:", err);
        const errorMessage = err.response?.data || err.message || "Błąd podczas usuwania dyżuru!";
        alert(typeof errorMessage === 'string' ? errorMessage : "Błąd podczas usuwania dyżuru!");
      });
  };

  useEffect(() => {
    fetchData();
  }, []);

  return (
    <ShiftPanelContainer>
      <TableStyled>
        <TableCaption>Lista dyżurów</TableCaption>
        <TableHeader>
          <TableRow>
            <TableHead>Lekarz</TableHead>
            <TableHead>Gabinet</TableHead>
            <TableHead>Dzień</TableHead>
            <TableHead>Godzina rozpoczęcia</TableHead>
            <TableHead>Godzina zakończenia</TableHead>
            <TableHead className="text-right"></TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {loading && (
            <TableRow>
              <TableCell colSpan={6} style={{ textAlign: "center" }}>
                Ładowanie...
              </TableCell>
            </TableRow>
          )}
          {!loading && dataArray.length === 0 && (
            <TableRow>
              <TableCell colSpan={6} style={{ textAlign: "center" }}>
                Brak dyżurów w bazie
              </TableCell>
            </TableRow>
          )}
          {!loading && dataArray.map((shift) => (
            <TableRow key={shift.id}>
              <TableCell>
                {shift.doctor.name} {shift.doctor.surname}
              </TableCell>
              <TableCell>Pokój {shift.office.roomNumber}</TableCell>
              <TableCell>{formatDayOfWeek(shift.dayOfWeek)}</TableCell>
              <TableCell>{formatTime(shift.startTime)}</TableCell>
              <TableCell>{formatTime(shift.endTime)}</TableCell>
              <TableCell className="text-right">
                <Button
                  variant="outline"
                  size="sm"
                  className="rounded-full w-8 h-8"
                  onClick={() => deleteShift(shift.id)}
                >
                  <X />
                </Button>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </TableStyled>
    </ShiftPanelContainer>
  );
};

export default ShiftPanel;
