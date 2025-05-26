import ModalEdit from "@/components/modals/modalEdit";
import { updatePoll } from "@/libs/api";
import PollSimpleView from "@/libs/polls/PollSimpleView";
import PollUpdateDTO from "@/libs/polls/PollUpdateDTO";
import UserQueryView from "@/libs/users/UserQueryView";
import { formatDistance } from "date-fns";
import { ptBR } from "date-fns/locale";
import Link from "next/link";
import { useState } from "react";
import { IoPersonCircle } from "react-icons/io5";

interface Props {
  poll?: PollSimpleView;
  user: UserQueryView | null;
  now: number;
}

export default function PollCard({ poll, user, now }: Props) {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [pollData, setPollData] = useState({
    title: poll?.title || "",
    description: poll?.description || "",
    startDate: poll?.startDate || "",
    endDate: poll?.endDate || "",
  });

  if (poll === undefined) {
    return <></>;
  }

  const startDate = Date.parse(pollData.startDate);
  const endDate = Date.parse(pollData.endDate);
  const nowDate = now;

  const handleSave = async (data: typeof pollData) => {
    const updateDto: PollUpdateDTO = {
      title: data.title,
      description: data.description,
      startDate: data.startDate,
      endDate: data.endDate,
    };

    try {
      const response = await updatePoll(poll.id, updateDto);
      if(response?.data){
        alert("Enquete atualizada com sucesso!");
        return setPollData(response.data);
      }
      alert("Erro inesperado ao atualizar votação!");
    } catch {
      alert("Erro ao atualizar votação!");
    }
  };

  const statusLabel =
      endDate < nowDate
          ? "Terminado "
          : startDate > nowDate
              ? "Começa "
              : "Termina ";

  const statusDate = startDate > nowDate ? startDate : endDate;

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
            {statusLabel}
            {formatDistance(statusDate, nowDate, {
              locale: ptBR,
              addSuffix: true,
            })}
          </p>
        </div>

        <Link
            id={`poll-card-${poll.id}-link`}
            href={`/polls/${poll.id}`}
            className="flex flex-col gap-3"
        >
          <h2 className="font-bold text-md">{pollData.title}</h2>
          <p>{pollData.description}</p>
        </Link>

        <div className="flex flex-col gap-3">
          <button
              onClick={() => setIsModalOpen(true)}
              className="self-end px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
          >
            Editar
          </button>

          {isModalOpen  && (<ModalEdit
              isOpen={isModalOpen}
              onClose={() => setIsModalOpen(false)}
              onSave={handleSave}
              initialData={pollData}
              now={new Date(now).toISOString()}
          />)}
        </div>
      </div>
  );
}
