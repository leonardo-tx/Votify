import { ReactNode } from "react";
import SocketProvider from "./SocketProvider";
import CurrentUserProvider from "./CurrentUserProvider";

interface Props {
  children: ReactNode;
}

export default function GlobalValuesProvider({ children }: Props) {
  return (
    <CurrentUserProvider>
      <SocketProvider>{children}</SocketProvider>
    </CurrentUserProvider>
  );
}
