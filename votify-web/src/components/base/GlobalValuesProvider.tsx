import SocketProvider from "./SocketProvider";
import CurrentUserProvider from "./CurrentUserProvider";
import { ReactNode } from "react";

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
