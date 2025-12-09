import { Label } from "@/components/ui/label";
import { Switch } from "@/components/ui/switch";
import { useTheme } from "../ThemeProvider";

export function DarkModeSwitch() {
  const theme = useTheme();

  const onCheckedChange = (checked: boolean) => {
    console.log("DarkModeSwitch onCheckedChange:", checked);
    theme.setTheme(checked ? "dark" : "light");
  };

  return (
    <div className="flex items-center space-x-2">
      <Switch id="dark-mode" onCheckedChange={onCheckedChange} />
      <Label htmlFor="dark-mode">Dark Mode</Label>
    </div>
  );
}
