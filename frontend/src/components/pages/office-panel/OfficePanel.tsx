import { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import type { Office } from "@/types/office";
import styled from "styled-components";
import { Button } from "@/components/ui/button";
import { Eye, Plus, X } from "lucide-react";
import {
  Item,
  ItemActions,
  ItemContent,
  ItemTitle,
} from "@/components/ui/item";
import NewOfficePanel from "./NewOfficePanel";
import { OFFICE_DETAILS_ROUTE } from "@/text/routes";

const OfficePanelStyled = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
`;

const InnerOfficePanelStyled = styled.div`
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 20px;
  border: 1px solid #ccc;
  padding: 20px;
  border-radius: 8px;
`;

const OfficePanel = () => {
  const navigate = useNavigate();
  const [addOfficeOpen, setAddOfficeOpen] = useState(false);
  const [dataArray, setDataArray] = useState<Office[]>([]);
  const [loading, setLoading] = useState(false);

  const getDetailsPage = (id: number) => {
    navigate(`${OFFICE_DETAILS_ROUTE}/${id}`);
  };

  const fetchData = () => {
    setLoading(true);
    axios
      .get("http://localhost:8080/offices", { timeout: 10000 })
      .then((response: any) => {
        if (response.data && Array.isArray(response.data)) {
          setDataArray(response.data);
        } else {
          setDataArray([]);
        }
        setLoading(false);
      })
      .catch((err: any) => {
        console.error("Error fetching offices:", err);
        setLoading(false);
        if (err.code === 'ECONNABORTED' || err.message?.includes('timeout')) {
          alert("Timeout - serwer nie odpowiada!");
        } else if (err.code === 'ERR_NETWORK' || err.message?.includes('Network Error')) {
          alert("Błąd połączenia z serwerem!");
        } else {
          alert("Błąd podczas pobierania listy gabinetów!");
        }
        setDataArray([]);
      });
  };

  const deleteOffice = (id: number) => {
    if (!window.confirm("Czy na pewno chcesz usunąć ten gabinet?")) {
      return;
    }
    
    axios
      .delete(`http://localhost:8080/offices/${id}`, { timeout: 10000 })
      .then(() => {
        fetchData();
      })
      .catch((err: any) => {
        console.error("Error deleting office:", err);
        const errorMessage = err.response?.data || err.message || "Błąd podczas usuwania gabinetu!";
        alert(typeof errorMessage === 'string' ? errorMessage : "Błąd podczas usuwania gabinetu!");
      });
  };

  useEffect(() => {
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <OfficePanelStyled>
      {addOfficeOpen ? (
        <NewOfficePanel
          dataArray={dataArray}
          setDataArray={setDataArray}
          setAddOfficeOpen={setAddOfficeOpen}
        />
      ) : (
        <InnerOfficePanelStyled>
          <Button
            variant="outline"
            size="icon"
            aria-label="Add"
            onClick={() => setAddOfficeOpen(true)}
          >
            <Plus />
          </Button>
          {loading && <div style={{ textAlign: "center", padding: "20px" }}>Ładowanie...</div>}
          {!loading && dataArray.length === 0 && (
            <div style={{ textAlign: "center", padding: "20px" }}>Brak gabinetów w bazie</div>
          )}
          {!loading && dataArray.map(({ id, roomNumber }) => (
            <Item variant="outline" size="sm" key={id} className="mb-4">
              <ItemContent>
                <ItemTitle>Room number: {roomNumber}</ItemTitle>
              </ItemContent>
              <ItemActions>
                <Button
                  variant="outline"
                  size="sm"
                  className="rounded-full w-8 h-8"
                  onClick={() => getDetailsPage(id)}
                >
                  <Eye />
                </Button>
                <Button
                  variant="outline"
                  size="sm"
                  className="rounded-full w-8 h-8"
                  onClick={() => deleteOffice(id)}
                >
                  <X />
                </Button>
              </ItemActions>
            </Item>
          ))}
        </InnerOfficePanelStyled>
      )}
    </OfficePanelStyled>
  );
};
export default OfficePanel;
