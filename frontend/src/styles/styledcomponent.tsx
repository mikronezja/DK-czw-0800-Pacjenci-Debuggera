import { Table } from "@/components/ui/table";
import { SelectTrigger } from "@radix-ui/react-select";
import styled from "styled-components";

export const SelectTriggerStyled = styled(SelectTrigger)`
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 5px 15px;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
`;

export const FormStyled = styled.form`
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  gap: 10px;
`;

export const TableStyled = styled(Table)`
  overflow-y: auto;
  height: 100%;
`;

export const TableDetailsStyled = styled(Table)`
  padding: 30px;
  margin: 30px auto;
  min-width: 400px;
  width: auto;
`;

export const Divider = styled.div`
  border-bottom: 1px solid #e0e0e0;
`;

export const SectionTitle = styled.div`
  display: flex;
  justify-content: center;
  margin: 20px;
`;
