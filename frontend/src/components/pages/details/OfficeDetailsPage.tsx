import axios from "axios";
import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import styled from "styled-components";
import {
  Table,
  TableBody,
  TableCaption,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";

const TableStyled = styled(Table)`
  justify-content: center;
  align-items: center;
  display: flex;
  flex-direction: column;
  padding: 30px;
  margin: 30px auto;
  min-width: 400px;
  width: auto;
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

interface OfficeShift {
  doctor: {
    id: number;
    name: string;
    surname: string;
    specialization: string;
    address: string;
  };
  dayOfWeek: string;
  startTime: { hour: number; minute: number; second: number; nano: number };
  endTime: { hour: number; minute: number; second: number; nano: number };
}

const OfficeDetailsPage = () => {
  const { idValue } = useParams();
  const [office, setOffice] = useState<{ id: number; roomNumber: number } | null>(null);
  const [shifts, setShifts] = useState<OfficeShift[]>([]);

  useEffect(() => {
    if (!idValue) {
      alert("Brak ID gabinetu!");
      return;
    }
    
    const id = Number(idValue);
    if (isNaN(id) || id <= 0) {
      alert("Nieprawidłowe ID gabinetu!");
      return;
    }

    // Pobierz szczegóły gabinetu
    axios
      .get(`http://localhost:8080/offices`, { timeout: 10000 })
      .then((res: any) => {
        if (res.data && Array.isArray(res.data)) {
          const foundOffice = res.data.find((o: { id: number }) => o.id === id);
          if (foundOffice) {
            setOffice(foundOffice);
          } else {
            alert("Gabinet nie został znaleziony!");
          }
        }
      })
      .catch((err: any) => {
        console.error("Error fetching office:", err);
        if (err.code === 'ECONNABORTED' || err.message?.includes('timeout')) {
          alert("Timeout - serwer nie odpowiada!");
        } else if (err.code === 'ERR_NETWORK' || err.message?.includes('Network Error')) {
          alert("Błąd połączenia z serwerem!");
        } else {
          alert("Błąd podczas pobierania danych gabinetu!");
        }
      });

    // Pobierz dyżury gabinetu
    axios
      .get(`http://localhost:8080/offices/${id}/shifts`, { timeout: 10000 })
      .then((res: any) => {
        if (res.data && Array.isArray(res.data)) {
          setShifts(res.data);
        } else {
          setShifts([]);
        }
      })
      .catch((err: any) => {
        console.error("Error fetching office shifts:", err);
        if (err.response?.status === 404) {
          setShifts([]);
        } else if (err.code === 'ECONNABORTED' || err.message?.includes('timeout')) {
          alert("Timeout - serwer nie odpowiada!");
        } else if (err.code === 'ERR_NETWORK' || err.message?.includes('Network Error')) {
          alert("Błąd połączenia z serwerem!");
        } else {
          alert("Błąd podczas pobierania dyżurów!");
        }
        setShifts([]);
      });
  }, [idValue]);

  if (!office) {
    return <div>Ładowanie...</div>;
  }

  return (
    <TableStyled>
      <div>
        <TableHeader>
          <TableRow>
            <TableHead colSpan={4}>Gabinet - Pokój {office.roomNumber}</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          <TableRow>
            <TableCell colSpan={4}>
              <strong>Numer pokoju:</strong> {office.roomNumber}
            </TableCell>
          </TableRow>
        </TableBody>
      </div>
      {shifts.length > 0 && (
        <div style={{ marginTop: "30px" }}>
          <TableHeader>
            <TableRow>
              <TableHead>Lekarz</TableHead>
              <TableHead>Dzień</TableHead>
              <TableHead>Godzina rozpoczęcia</TableHead>
              <TableHead>Godzina zakończenia</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {shifts.map((shift, index) => (
              <TableRow key={`${shift.doctor.id}-${shift.dayOfWeek}-${index}`}>
                <TableCell>
                  {shift.doctor.name} {shift.doctor.surname} ({shift.doctor.specialization})
                </TableCell>
                <TableCell>{formatDayOfWeek(shift.dayOfWeek)}</TableCell>
                <TableCell>{formatTime(shift.startTime)}</TableCell>
                <TableCell>{formatTime(shift.endTime)}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </div>
      )}
      {shifts.length === 0 && (
        <div style={{ marginTop: "30px", textAlign: "center" }}>
          Brak przypisanych dyżurów
        </div>
      )}
    </TableStyled>
  );
};

export default OfficeDetailsPage;
