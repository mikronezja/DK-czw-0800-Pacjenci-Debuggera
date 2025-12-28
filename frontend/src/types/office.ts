import type { Shift } from "./shifts";

export interface Office {
  id: number;
  roomNumber: number;
  shifts: Shift[];
}
