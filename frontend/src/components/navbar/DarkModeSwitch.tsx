import { Label } from "@/components/ui/label";
import { Switch } from "@/components/ui/switch";
import { useTheme } from "../ThemeProvider";

export function DarkModeSwitch() {
  const theme = useTheme();

  const onCheckedChange = (checked: boolean) => {
    theme.setTheme(checked ? "light" : "dark");
  };

  return (
    <div className="flex items-center space-x-2">
      <Switch id="dark-mode" onCheckedChange={onCheckedChange} />
      <Label htmlFor="dark-mode">
        Tryb {theme.theme === "light" ? "ciemny" : "jasny"}
      </Label>
    </div>
  );
}
