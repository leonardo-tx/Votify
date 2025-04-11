import { socketAtom } from "@/libs/socket/atoms/socketAtom";
import { Client } from "@stomp/stompjs";
import { useSetAtom } from "jotai";
import { ReactNode, useEffect } from "react";

interface Props {
  children: ReactNode;
}

export default function SocketProvider({ children }: Props) {
  const setSocket = useSetAtom(socketAtom);

  useEffect(() => {
    const createdClient = new Client({
      brokerURL: "ws://localhost:8081/ws",
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      webSocketFactory: () =>
        new WebSocket("ws://" + process.env.NEXT_PUBLIC_API_BASE_URL + "/ws"),
    });
    setSocket(createdClient);

    return () => {
      createdClient.deactivate();
      setSocket(null);
    };
  }, [setSocket]);

  return children;
}
