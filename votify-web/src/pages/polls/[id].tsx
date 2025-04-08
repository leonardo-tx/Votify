import { GetServerSideProps } from "next";
import axios from "axios";

interface VoteOption {
    name: string;
    voteCount: number;
}

interface Poll {
    id: number;
    title: string;
    description: string;
    startDate: string;
    endDate: string;
    choiceLimitPerUser: number;
    responsibleId: number;
    voteOptions: VoteOption[];
    myChoices: number;
}


interface ApiResponse {
    success: boolean;
    data: Poll;
    errorCode: number | null;
    errorMessage: string | null;
}

interface PollPageProps {
    poll: Poll | null;
    isAuthenticated: boolean;
}

export default function PollDetailPage({ poll, isAuthenticated }: PollPageProps) {
    if (!poll) {
        return (
            <div className="flex justify-center items-center h-screen bg-neutral-900 text-white">
                <p>Enquete não encontrada.</p>
            </div>
        );
    }

    return (
        <div className="bg-neutral-900 min-h-screen text-white flex flex-col items-center justify-center p-4">
            <div className="w-full max-w-xl bg-neutral-800 rounded-lg shadow-md p-6">
                <h1 className="text-2xl font-extrabold mb-3 text-center">{poll.title}</h1>
                <p className="text-base text-center text-gray-300 mb-4">
                    {poll.description}
                </p>

                <p className="text-sm text-gray-400 text-center mb-6">
                    Início: {new Date(poll.startDate).toLocaleString()} <br />
                    Fim: {new Date(poll.endDate).toLocaleString()}
                </p>

                <div className="mb-6">
                    <h2 className="text-xl font-bold mb-3">Opções de Voto</h2>
                    <form className="flex flex-col gap-3">
                        {poll.voteOptions.map((option, idx) => (
                            <label
                                key={idx}
                                className="flex items-center gap-2 bg-neutral-700 rounded p-3"
                            >
                                <input
                                    type="radio"
                                    name="pollOption"
                                    value={option.name}
                                    className="accent-blue-500"
                                    disabled={!isAuthenticated}
                                />
                                <span className="flex-1">{option.name}</span>
                                <span className="text-sm text-gray-300">
                  {option.voteCount} voto{option.voteCount === 1 ? "" : "s"}
                </span>
                            </label>
                        ))}
                    </form>
                </div>

                <button
                    disabled={!isAuthenticated}
                    className={`w-full py-3 font-semibold rounded transition-colors
          ${
                        isAuthenticated
                            ? "bg-blue-600 hover:bg-blue-700 text-white"
                            : "bg-gray-600 text-gray-400 cursor-not-allowed"
                    }`}
                >
                    {isAuthenticated ? "Votar" : "Faça login para votar"}
                </button>
            </div>
        </div>
    );
}

export const getServerSideProps: GetServerSideProps<PollPageProps> = async ({
                                                                                params,
                                                                            }) => {
    // Exemplo simples: se tiver algum check de autenticação, injete aqui
    const isAuthenticated = false; // Simulando que o usuário não está logado

    const { id } = params as { id: string };

    try {
        const response = await axios.get<ApiResponse>(`http://localhost:8081/polls/${id}`);
        const poll = response.data.success ? response.data.data : null;

        return {
            props: {
                poll,
                isAuthenticated,
            },
        };
    } catch (error) {
        console.error("Erro ao buscar a enquete:", error);
        return {
            props: {
                poll: null,
                isAuthenticated,
            },
        };
    }
};
