import React from "react";
import * as SwitchPrimitive from "@radix-ui/react-switch";

type Props = React.ComponentProps<typeof SwitchPrimitive.Root> & {
  className?: string;
};

const Switch = React.forwardRef<HTMLButtonElement, Props>(
  ({ className = "", ...props }, ref) => {
    return (
      <SwitchPrimitive.Root
        ref={ref}
        className={
          "relative inline-flex items-center w-10 h-6 rounded-full transition-colors focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 bg-muted " +
          "data-[state=checked]:bg-primary " +
          className
        }
        {...props}
      >
        <SwitchPrimitive.Thumb
          className={
            "block w-4 h-4 bg-background rounded-full shadow transform transition-transform duration-200 " +
            "translate-x-0 data-[state=checked]:translate-x-4"
          }
          aria-hidden
        />
      </SwitchPrimitive.Root>
    );
  }
);

Switch.displayName = "Switch";

export { Switch };
