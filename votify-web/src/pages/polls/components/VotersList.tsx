
import { useState, useEffect, useCallback } from 'react';
import { getPollVoters } from '@/libs/api';
import { PageResponse } from '@/libs/PageResponse';
import UserQueryView from '@/libs/users/UserQueryView';


interface VotersListProps {
    pollId: number;
}

export default function VotersList({ pollId }: VotersListProps) {
    const [votersPage, setVotersPage] = useState<PageResponse<UserQueryView> | null>(null);
    const [currentPage, setCurrentPage] = useState(0);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const fetchVoters = useCallback(async (pageToFetch: number) => {
        setIsLoading(true);
        setError(null);
        try {
            const response = await getPollVoters(pollId, pageToFetch, 10);

            if (response.success && response.data) {
                setVotersPage(response.data);
                setCurrentPage(response.data.pageNumber);
            } else {
                setError(response.errorMessage || 'Erro ao buscar a lista de participantes.');
            }
        } catch (err) {
            setError('Falha ao conectar com o servidor para buscar participantes.');
            console.error("Erro ao buscar votantes:", err);
        } finally {
            setIsLoading(false);
        }
    }, [pollId]);

    useEffect(() => {
        if (pollId) {
            fetchVoters(0);
        }
    }, [fetchVoters, pollId]);

    const handlePageChange = (newPage: number) => {
        if (newPage >= 0 && (!votersPage || newPage < votersPage.totalPages)) {
            fetchVoters(newPage);
        }
    };

    if (isLoading && !votersPage) {
        return <p className="text-gray-600">Carregando participantes...</p>;
    }

    if (error) {
        return <p className="text-red-500">Erro: {error}</p>;
    }

    if (!votersPage || votersPage.content.length === 0) {
        return <p className="text-gray-600">Nenhum participante registrado para esta enquete ou nenhum voto computado.</p>;
    }

    return (
        <div className="space-y-4">
            <ul  id="voters-list-ul" className="space-y-2">
                {votersPage.content.map((voter) => (
                    <li key={voter.id} className="py-3 px-2 border-b border-gray-700 last:border-b-0">
                        <p className="font-semibold text-gray-200">{voter.name} <span className="text-sm text-gray-400">(@{voter.userName})</span></p>
                        <p className="text-sm text-gray-400">{voter.email}</p>
                    </li>
                ))}
            </ul>


            {votersPage.totalPages > 1 && (
                <div className="pagination-controls mt-6 flex justify-center items-center space-x-3">
                    <button
                        onClick={() => handlePageChange(currentPage - 1)}
                        disabled={votersPage.first || isLoading}
                        className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                        Anterior
                    </button>
                    <span className="text-sm text-gray-700">
            Página {votersPage.pageNumber + 1} de {votersPage.totalPages}
          </span>
                    <button
                        onClick={() => handlePageChange(currentPage + 1)}
                        disabled={votersPage.last || isLoading}
                        className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                        Próxima
                    </button>
                </div>
            )}
        </div>
    );
}