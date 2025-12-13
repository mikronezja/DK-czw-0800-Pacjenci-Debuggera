export interface Doctor {
  name: string;
  surname: string;
  specialization: string;
  pesel: string | undefined;
  address: string | undefined;
  id: number;
}

export interface Pacient {
  name: string;
  surname: string;
  pesel: string | undefined;
  address: string | undefined;
  id: number;
}
