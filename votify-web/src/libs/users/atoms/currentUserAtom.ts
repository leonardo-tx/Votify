import { atom } from "jotai";
import UserDetailedView from "../UserDetailedView";

export const currentUserAtom = atom<UserDetailedView | null>(null);
