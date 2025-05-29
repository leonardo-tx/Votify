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
  showUser: boolean;
}

export default function PollCard({ poll, user, now, showUser }: Props) {
  if (poll === undefined) {
    return <></>;
  }

  const startDate = Date.parse(poll.startDate);
  const endDate = Date.parse(poll.endDate);

  return (
    <div
      id={`poll-card-${poll.id}`}
      className="bg-(--card-bg) rounded-md p-5 shadow-md flex flex-col gap-5"
    >
      <div className="flex items-center justify-between">
        {showUser && (
          <Link
            id={`poll-card-user-profile-link-${poll.id}`}
            href={user?.userName ? `/profile/${user.userName}` : "#"}
            className={`flex gap-2 items-center ${user?.userName ? "hover:text-(--foreground-hover) cursor-pointer" : "cursor-default"}`}
            onClick={(e) => {
              if (!user?.userName) e.preventDefault();
            }}
          >
            <IoPersonCircle size={25} />
            <p className="font-normal text-sm">
              {user?.name ?? "Usuário Desconhecido"}
            </p>
          </Link>
        )}
        <p className="font-normal text-sm">
          {endDate < now
            ? "Terminado "
            : startDate > now
              ? "Começa "
              : "Termina "}
          {formatDistance(startDate > now ? startDate : endDate, now, {
            locale: ptBR,
            addSuffix: true,
          })}
        </p>
      </div>
      <Link
        id={`poll-card-link-${poll.id}`}
        href={`/polls/${poll.id}`}
        className="flex flex-col gap-3"
      >
        <h2 className="font-bold text-md">{poll.title}</h2>
        <p>{poll.description}</p>
      </Link>
    </div>
  );
}
