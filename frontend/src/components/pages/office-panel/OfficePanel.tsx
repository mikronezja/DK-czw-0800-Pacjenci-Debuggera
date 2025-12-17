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

  const getDetailsPage = (id: number) => {
    navigate(`${OFFICE_DETAILS_ROUTE}/${id}`);
  };

  const deleteOffice = (id: number) => {
    axios
      .delete(`http://localhost:8080/offices/${id}`)
      .then((res) => {
        setDataArray(
          dataArray.filter((office: { id: number }) => office.id !== id)
        );
        console.log("office deleted!", res.data);
      })
      .catch((err) => console.error(err));
  };

  useEffect(() => {
    axios.get("http://localhost:8080/offices").then((response) => {
      console.log("offices fetched:", response.data);
      setDataArray(response.data);
    });
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
          {dataArray.map(({ id, roomNumber }, index) => (
            <Item variant="outline" size="sm" key={index} className="mb-4">
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
