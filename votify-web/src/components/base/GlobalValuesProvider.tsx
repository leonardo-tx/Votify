import { getCurrentUser } from "@/libs/api";
import { currentUserAtom } from "@/libs/users/atoms/currentUserAtom";
import { useSetAtom } from "jotai";
import { ReactNode, useEffect } from "react";

interface Props {
  children: ReactNode;
}

export default function GlobalValuesProvider({ children }: Props) {
  const setCurrentUser = useSetAtom(currentUserAtom);

  useEffect(() => {
    const loadCurrentUser = async () => {
      setCurrentUser((await getCurrentUser()).data);
    };
    loadCurrentUser();
  }, [setCurrentUser]);

  return children;
}
