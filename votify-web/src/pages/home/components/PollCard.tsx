import PollSimpleView from "@/libs/polls/PollSimpleView";
import UserQueryView from "@/libs/users/UserQueryView";
import { formatDistance } from "date-fns";
import { ptBR } from "date-fns/locale";
import Link from "next/link";
import { IoPersonCircle } from "react-icons/io5";

interface Props {
  poll?: PollSimpleView;
  user: UserQueryView | null;
  now: number;
}

export default function PollCard({ poll, user, now }: Props) {
  if (poll === undefined) {
    return <></>;
  }

  const startDate = Date.parse(poll.startDate);
  const endDate = Date.parse(poll.endDate);
  const nowDate = new Date(now);

  return (
    <div
      id={`poll-card-${poll.id}`}
      className="bg-(--card-bg) rounded-md p-5 shadow-md flex flex-col gap-5"
    >
      <div className="flex items-center justify-between">
        <Link
          id={`poll-card-${poll.id}-user-profile-link`}
          href={`/home`}
          className="flex gap-2 items-center hover:text-(--foreground-hover)"
        >
          <IoPersonCircle size={25} />
          <p className="font-normal text-sm">{user?.name ?? "Desconhecido"}</p>
        </Link>
        <p className="font-normal text-sm">
          {endDate < nowDate.getTime()
            ? "Terminado "
            : startDate > nowDate.getTime()
              ? "Começa "
              : "Termina "}
          {formatDistance(
            startDate > nowDate.getTime() ? startDate : endDate,
            nowDate,
            { locale: ptBR, addSuffix: true },
          )}
        </p>
      </div>
      <Link
        id={`poll-card-${poll.id}-link`}
        href={`/polls/${poll.id}`}
        className="flex flex-col gap-3"
      >
        <h2 className="font-bold text-md">{poll.title}</h2>
        <p>{poll.description}</p>
      </Link>
    </div>
  );
}
