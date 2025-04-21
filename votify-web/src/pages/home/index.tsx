import PollSimpleView from "@/libs/polls/PollSimpleView";
import { getUserById, searchPollsByTitle } from "@/libs/api";
import { GetServerSideProps } from "next";
import UserQueryView from "@/libs/users/UserQueryView";
import PollList from "./components/PollList";
import { useEffect, useState } from "react";
import { useAtom } from "jotai";
import { searchTermAtom } from "@/libs/polls/atoms/searchTermAtom";
import VotifyErrorCode from "@/libs/VotifyErrorCode";
import Button from "@/components/shared/Button";

interface Props {
  initialPolls: { poll: PollSimpleView; user: UserQueryView | null }[];
}

export default function Home({ initialPolls }: Props) {
  const [polls, setPolls] = useState(initialPolls);
  const [searchTerm] = useAtom(searchTermAtom);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const pageSize = 10;

  useEffect(() => {

    setCurrentPage(0);
    handleSearch(0);
  }, [searchTerm]);

  const handleSearch = async (page: number) => {
    setLoading(true);
    setError(null);
    const query = searchTerm?.trim() || "";
    
    const response = await searchPollsByTitle(query, page, pageSize);
    
    if (response && response.data) {
      const pollsWithUsers = await Promise.all(
        response.data.content.map(async (poll: PollSimpleView) => {
          const user = (await getUserById(poll.responsibleId)).data;
          return { poll, user };
        })
      );
      setPolls(pollsWithUsers);
      setCurrentPage(response.data.pageNumber);
      setTotalPages(response.data.totalPages);
    } else if (!response.success) {
      if (response.errorCode === VotifyErrorCode.POLL_TITLE_SEARCH_EMPTY) {
        setError("Por favor, informe um título não vazio ou nulo para pesquisa.");
        setPolls([]);
      } else {
        setError(`Erro na busca: ${response.errorMessage}`);
        setPolls([]);
      }
    }

    setLoading(false);
  };

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
    handleSearch(page);
  };

  const Pagination = ({ currentPage, totalPages, onPageChange }: { currentPage: number, totalPages: number, onPageChange: (page: number) => void }) => {
    if (totalPages <= 1) return null;
    
    return (
      <div className="flex justify-center mt-4 space-x-2">
        <Button
          onClick={() => onPageChange(Math.max(0, currentPage - 1))}
          disabled={currentPage === 0}
          className={currentPage === 0 ? 'opacity-50 cursor-not-allowed' : 'cursor-pointer'}
          variant="outline"
          scheme="primary"
        >
          Anterior
        </Button>
        
        <span className="px-3 py-1">
          Página {currentPage + 1} de {totalPages}
        </span>
        
        <Button
          onClick={() => onPageChange(Math.min(totalPages - 1, currentPage + 1))}
          disabled={currentPage >= totalPages - 1}
          className={currentPage >= totalPages - 1 ? 'opacity-50 cursor-not-allowed' : 'cursor-pointer'}
          variant="outline"
          scheme="primary"
        >
          Próximo
        </Button>
      </div>
    );
  };

  return (
    <div className="flex flex-col gap-4">
      <div>
        <h2 className="text-xl font-bold mb-2">
          {searchTerm ? `Resultados da busca por "${searchTerm}"` : "Todas as enquetes"}
        </h2>
        {loading ? (
          <p>Carregando...</p>
        ) : error ? (
          <div className="p-4 bg-red-100 text-red-800 rounded-md">
            {error}
          </div>
        ) : (
          <>
            <PollList polls={polls} />
            <Pagination 
              currentPage={currentPage} 
              totalPages={totalPages} 
              onPageChange={handlePageChange} 
            />
          </>
        )}
      </div>
    </div>
  );
}

export const getServerSideProps: GetServerSideProps<Props> = async () => {
  try {
    const response = await searchPollsByTitle("a", 0, 10);
    let pollsWithUserData: { poll: PollSimpleView; user: UserQueryView | null }[] = [];
    
    if (response && response.data && response.data.content) {
      const users: Map<number, UserQueryView> = new Map();
      
      pollsWithUserData = await Promise.all(
        response.data.content.map(async (poll: PollSimpleView) => {
          const userFromMap = users.get(poll.responsibleId);
          if (userFromMap !== undefined) {
            return { poll, user: userFromMap };
          }

          const user = (await getUserById(poll.responsibleId)).data;
          if (user !== null) {
            users.set(user.id, user);
          }
          return { poll, user };
        })
      );
    }

    return {
      props: {
        initialPolls: pollsWithUserData,
      },
    };
  } catch (error) {
    console.error("Error fetching initial polls:", error);
    return {
      props: {
        initialPolls: [],
      },
    };
  }
};
