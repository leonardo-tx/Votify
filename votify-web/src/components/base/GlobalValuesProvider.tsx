import { getCurrentUser } from "@/libs/api";
import { currentUserAtom } from "@/libs/users/atoms/currentUserAtom";
import { useSetAtom } from "jotai";
import { ReactNode, useEffect, useState } from "react";

interface Props {
  children: ReactNode;
}

export default function GlobalValuesProvider({ children }: Props) {
  const setCurrentUser = useSetAtom(currentUserAtom);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadCurrentUser = async () => {
      setCurrentUser((await getCurrentUser()).data);
      setLoading(false);
    };
    loadCurrentUser();
  }, [setCurrentUser]);

  return !loading && children;
}
