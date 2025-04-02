import { ReactNode } from "react";

interface Props {
  children?: ReactNode;
}

export default function Main({ children }: Props) {
  return <main className="flex flex-col p-5">{children}</main>;
}
