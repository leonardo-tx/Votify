import { GetServerSideProps } from "next";
import { PollDetailedView } from "@/libs/polls/PollDetailedView";
import VoteForm from "./components/VoteForm";
import { useState, useEffect } from "react";
import { formatDistance } from "date-fns";
import { ptBR } from "date-fns/locale";
import Head from "next/head";
import { getPollById } from "@/libs/api";

interface PollPageProps {
  poll: PollDetailedView | null;
}

export default function PollDetailPage({ poll }: PollPageProps) {
  const [now, setNow] = useState(0);

  useEffect(() => {
    setNow(Date.now());
    const timer = setInterval(() => {
      setNow(Date.now());
    }, 1000);

    return () => clearInterval(timer);
  }, []);

  if (!poll) {
    return (
      <>
        <Head>
          <title>Enquete não encontrada - Votify</title>
        </Head>
        <div className="flex justify-center items-center h-full">
          <p id="no-poll-message">Enquete não encontrada.</p>
        </div>
      </>
    );
  }

  const startDate = Date.parse(poll.startDate);
  const endDate = Date.parse(poll.endDate);

  return (
    <>
      <Head>
        <title>Enquete: {poll.title} - Votify</title>
      </Head>
      <div className="h-full flex flex-col items-center justify-center p-4">
        <div className="w-full max-w-3xl shadow-lg rounded-lg p-6">
          <h1 className="text-2xl font-extrabold mb-3">{poll.title}</h1>
          <p className="text-base mb-4 whitespace-pre-line">
            {poll.description}
          </p>
          <p className="text-sm text-gray-500 text-center mb-6">
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

          <div className="mb-6">
            <VoteForm poll={poll} />
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
  const response = await getPollById(parseInt(id), req.headers.cookie);

  return { props: { poll: response.data } };
};
