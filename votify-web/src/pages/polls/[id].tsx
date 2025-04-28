import { useState } from "react";
import { GetServerSideProps } from "next";
import { useAtomValue } from "jotai";
import { currentUserAtom } from "@/libs/users/atoms/currentUserAtom";
import { api } from "@/libs/api";
import Button from "@/components/shared/Button";
import { PollDetailedView } from "@/libs/polls/PollDetailedView";
import type ApiResponse from "@/libs/ApiResponse";

interface PollPageProps {
    poll: PollDetailedView | null;
}

export default function PollDetailPage({ poll }: PollPageProps) {
    const currentUser = useAtomValue(currentUserAtom);
    const isAuthenticated = !!currentUser;

    if (!poll) {
        return (
            <div className="flex justify-center items-center h-screen bg-neutral-900 text-white">
                <p>Enquete não encontrada.</p>
            </div>
        );
    }

    const { myChoices, voteOptions, choiceLimitPerUser } = poll;
    const hasVoted = myChoices > 0;
    const votedIndices = voteOptions
        .map((_, idx) => ((myChoices & (1 << idx)) !== 0 ? idx : -1))
        .filter(idx => idx >= 0);

    const isMultiple = choiceLimitPerUser > 1;

    const [selectedIdx, setSelectedIdx] = useState<number | null>(null);
    const [selectedIndices, setSelectedIndices] = useState<number[]>([]);

    const toggleIndex = (idx: number) => {
        setSelectedIndices(prev =>
            prev.includes(idx) ? prev.filter(i => i !== idx) : [...prev, idx]
        );
    };

    const handleVote = async () => {
        let bitmask: number;
        if (isMultiple) {
            if (selectedIndices.length === 0) return;
            bitmask = selectedIndices.reduce((mask, i) => mask | (1 << i), 0);
        } else {
            if (selectedIdx === null) return;
            bitmask = 1 << selectedIdx;
        }

        try {
            const { data } = await api.post<ApiResponse<number>>(
                `/polls/${poll.id}/vote`,
                { value: bitmask }
            );
            if (data.success) {
                window.location.reload();
            } else {
                console.error(data.errorCode, data.errorMessage);
            }
        } catch (err: any) {
            console.error("Erro de conexão ao votar:", err.response || err);
        }
    };

    return (
        <div className="bg-neutral-900 min-h-screen text-white flex flex-col items-center justify-center p-4">
            <div className="w-full max-w-xl bg-neutral-800 rounded-lg shadow-md p-6">
                <h1 className="text-2xl font-extrabold mb-3 text-center">
                    {poll.title}
                </h1>
                <p className="text-base text-center text-gray-300 mb-4">
                    {poll.description}
                </p>
                <p className="text-sm text-gray-400 text-center mb-6">
                    Início: {new Date(poll.startDate).toLocaleString()} <br />
                    Fim: {new Date(poll.endDate).toLocaleString()}
                </p>

                <div className="mb-6">
                    <h2 className="text-xl font-bold mb-3">Opções de Voto</h2>

                    {hasVoted ? (
                        <form className="flex flex-col gap-3">
                            {voteOptions.map((opt, idx) => (
                                <label
                                    key={idx}
                                    className="flex items-center gap-2 bg-neutral-700 rounded p-3"
                                >
                                    <input
                                        type={isMultiple ? "checkbox" : "radio"}
                                        name="pollOption"
                                        value={idx}
                                        disabled={true}
                                        checked={votedIndices.includes(idx)}
                                        className={`
                      h-4 w-4 rounded-full border-2 transition-colors
                      ${hasVoted ? "border-gray-400" : "border-blue-500"}
                    `}
                                    />
                                    <span className="flex-1">{opt.name}</span>
                                    <span className="text-sm text-gray-300">
                    {opt.voteCount} voto{opt.voteCount === 1 ? "" : "s"}
                  </span>
                                </label>
                            ))}
                        </form>
                    ) : (
                        // Antes de votar: form normal (radio ou checkbox)
                        <form className="flex flex-col gap-3">
                            {voteOptions.map((opt, idx) => (
                                <label
                                    key={idx}
                                    className="flex items-center gap-2 bg-neutral-700 rounded p-3"
                                >
                                    <input
                                        type={isMultiple ? "checkbox" : "radio"}
                                        name="pollOption"
                                        value={idx}
                                        disabled={!isAuthenticated}
                                        checked={
                                            isMultiple
                                                ? selectedIndices.includes(idx)
                                                : selectedIdx === idx
                                        }
                                        onChange={() =>
                                            isMultiple ? toggleIndex(idx) : setSelectedIdx(idx)
                                        }
                                        className={`
                      h-4 w-4 rounded-full border-2 transition-colors
                      ${hasVoted ? "border-gray-400" : "border-blue-500"}
                    `}
                                    />
                                    <span className="flex-1">{opt.name}</span>
                                    <span className="text-sm text-gray-300">
                    {opt.voteCount} voto{opt.voteCount === 1 ? "" : "s"}
                  </span>
                                </label>
                            ))}
                        </form>
                    )}
                </div>


                {!hasVoted && (
                    <Button
                        scheme="primary"
                        disabled={
                            !isAuthenticated ||
                            (isMultiple
                                ? selectedIndices.length === 0
                                : selectedIdx === null)
                        }
                        onClick={handleVote}
                        className="w-full py-3 font-semibold rounded transition-colors cursor-pointer disabled:cursor-not-allowed"
                    >
                        {isAuthenticated ? "Votar" : "Faça login para votar"}
                    </Button>
                )}
            </div>
        </div>
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
            { headers: { cookie: req.headers.cookie ?? "" } }
        );
        return { props: { poll: resp.data } };
    } catch {
        return { props: { poll: null } };
    }
};