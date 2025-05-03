import { Client } from "@stomp/stompjs";
import { atom } from "jotai";

export const socketAtom = atom<Client | null>(null);
