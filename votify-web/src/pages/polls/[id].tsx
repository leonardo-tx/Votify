import { GetServerSideProps } from "next";
import { api } from "@/libs/api";
import { PollDetailedView } from "@/libs/polls/PollDetailedView";
import type ApiResponse from "@/libs/ApiResponse";
import VoteForm from "./components/VoteForm";
import { useState, useEffect } from "react";
import { formatDistance } from "date-fns";
import { ptBR } from "date-fns/locale";
import Head from "next/head";

interface PollPageProps {
  poll: PollDetailedView | null;
}

export default function PollDetailPage({ poll }: PollPageProps) {
  const [now, setNow] = useState(Date.now());

  useEffect(() => {
    const timer = setInterval(() => {
      setNow(Date.now());
    }, 1000);

    return () => clearInterval(timer);
  }, []);

  if (!poll) {
    return (
      <div className="flex justify-center items-center h-full">
        <p id="no-poll-message">Enquete não encontrada.</p>
      </div>
    );
  }

  const nowDate = new Date(now);
  const startDate = Date.parse(poll.startDate);
  const endDate = Date.parse(poll.endDate);

  return (
    <>
      <Head>
        <title>Enquete: {poll.title} - Votify</title>
      </Head>
      <div className="h-full flex flex-col items-center justify-center p-4">
        <div className="w-full max-w-xl shadow-lg rounded-lg p-6">
          <h1 className="text-2xl font-extrabold mb-3">{poll.title}</h1>
          <p className="text-base mb-4 whitespace-pre-line">
            {poll.description}
          </p>
          <p className="text-sm text-gray-500 text-center mb-6">
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

          <div className="mb-6">
            <h2 className="text-xl font-bold mb-3">Opções de Voto</h2>
            <VoteForm
              pollId={poll.id}
              voteOptions={poll.voteOptions}
              choiceLimitPerUser={poll.choiceLimitPerUser}
              initialChoices={poll.myChoices}
            />
          </div>
        </div>
      </div>
    </>
  );
}

export const getServerSideProps: GetServerSideProps<PollPageProps> = async ({
  params,
  req,
}) => {
  const { id } = params as { id: string };
  try {
    const { data: resp } = await api.get<ApiResponse<PollDetailedView>>(
      `/polls/${id}`,
      { headers: { cookie: req.headers.cookie ?? "" } },
    );
    return { props: { poll: resp.data } };
  } catch {
    return { props: { poll: null } };
  }
};
