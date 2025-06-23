import ModalEdit from "@/components/modals/modalEdit";
import CancelConfirmationModal from "@/components/modals/CancelConfirmationModal";
import { updatePoll, cancelPoll } from "@/libs/api";
import PollSimpleView from "@/libs/polls/PollSimpleView";
import PollUpdateDTO from "@/libs/polls/PollUpdateDTO";
import UserQueryView from "@/libs/users/UserQueryView";
import { formatDistance } from "date-fns";
import { ptBR } from "date-fns/locale";
import Link from "next/link";
import { useState } from "react";
import { IoPersonCircle } from "react-icons/io5";
import { useAtom } from "jotai";
import { currentUserAtom } from "@/libs/users/atoms/currentUserAtom";

interface Props {
  poll?: PollSimpleView;
  user: UserQueryView | null;
  now: number;
  showUser: boolean;
  onCancel?: (id: number) => void;
}

export default function PollCard({ poll, user, now, showUser, onCancel }: Props) {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isCancelModalOpen, setIsCancelModalOpen] = useState(false);
  const [pollData, setPollData] = useState({
    title: poll?.title || "",
    description: poll?.description || "",
    startDate: poll?.startDate || "",
    endDate: poll?.endDate || "",
  });
  const [currentUser] = useAtom(currentUserAtom);

  if (!poll) return null;

  const startDate = Date.parse(pollData.startDate);
  const endDate = Date.parse(pollData.endDate);

  const handleSave = async (data: typeof pollData) => {
    const updateDto: PollUpdateDTO = {
      title: data.title,
      description: data.description,
      startDate: data.startDate,
      endDate: data.endDate,
    };
    try {
      const response = await updatePoll(poll.id, updateDto);
      if (response?.data) {
        alert("Enquete atualizada com sucesso!");
        setPollData(response.data);
      } else {
        alert("Erro inesperado ao atualizar votação!");
      }
    } catch {
      alert("Erro ao atualizar votação!");
    }
  };

  const handlerCancelPoll = async () => {
    try {
      await cancelPoll(poll.id);
      alert("Enquete cancelada com sucesso!");
      setIsCancelModalOpen(false);
      onCancel?.(poll.id);
    } catch {
      alert("Erro ao cancelar enquete!");
    }
  };

  const statusLabel =
      endDate < now
          ? "Terminou "
          : startDate > now
              ? "Começa em "
              : "Termina em ";

  const statusDate = startDate > now ? startDate : endDate;

  return (
      <div
          id={`poll-card-${poll.id}`}
          className="bg-[var(--card-bg)] rounded-md p-5 shadow-md flex flex-col gap-5"
      >
        <div className="flex items-center justify-between">
          {showUser &&
              (user === null ? (
                  <div className="flex gap-2 items-center">
                    <IoPersonCircle size={25} />
                    <p className="font-normal text-sm">Usuário Deletado</p>
                  </div>
              ) : (
                  <Link
                      id={`poll-card-${poll.id}-user-profile-link`}
                      href={`/profile/${user.userName}`}
                      className="flex gap-2 items-center hover:text-[var(--foreground-hover)]"
                  >
                    <IoPersonCircle size={25} />
                    <p className="font-normal text-sm">{user.name}</p>
                  </Link>
              ))}
          <p className="font-normal text-sm">
            {statusLabel}
            {formatDistance(statusDate, now, {
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
          {currentUser?.id === poll.responsibleId && (
              <div className="flex flex-col gap-3">
                <div className="flex gap-2 justify-end">
                  <button
                      onClick={() => setIsModalOpen(true)}
                      className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                  >
                    Editar
                  </button>

                  <button
                      onClick={() => setIsCancelModalOpen(true)}
                      className="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700"
                  >
                    Cancelar
                  </button>
                </div>
              </div>
          )}

          <ModalEdit
              isOpen={isModalOpen}
              onClose={() => setIsModalOpen(false)}
              onSave={handleSave}
              initialData={pollData}
              now={new Date(now).toISOString()}
          />

          <CancelConfirmationModal
              isOpen={isCancelModalOpen}
              onClose={() => setIsCancelModalOpen(false)}
              onConfirm={handlerCancelPoll}
          />
        </div>
      </div>
  );
}
