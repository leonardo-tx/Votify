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
      try {
        const response = await getCurrentUser();
        if (response.success) {
          setCurrentUser(response.data);
        } else {
          setCurrentUser(null);
        }
      } catch (error) {
        console.log("Erro de autenticação, prosseguindo sem usuário:", error);
        setCurrentUser(null);
      }
    };
    loadCurrentUser();
  }, [setCurrentUser]);

  return children;
}
