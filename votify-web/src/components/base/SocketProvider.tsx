import { socketAtom } from "@/libs/socket/atoms/socketAtom";
import { Client } from "@stomp/stompjs";
import { useAtom } from "jotai";
import { ReactNode, useEffect } from "react";
import SockJS from "sockjs-client";

interface Props {
  children: ReactNode;
}

export default function SocketProvider({ children }: Props) {
  const [socket, setSocket] = useAtom(socketAtom);

  useEffect(() => {
    const createdClient = new Client({
      brokerURL: `${window.location.origin}/ws`,
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      webSocketFactory: () => new SockJS(`${window.location.origin}/ws`),
    });
    setSocket(createdClient);

    return () => {
      createdClient.deactivate();
      setSocket(null);
    };
  }, [setSocket]);

  return socket !== null && children;
}
