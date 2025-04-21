import PollSimpleView from "@/libs/polls/PollSimpleView";
import { getUserById, searchPollsByTitle, getCurrentUser } from "@/libs/api";
import { GetServerSideProps } from "next";
import UserQueryView from "@/libs/users/UserQueryView";
import PollList from "./components/PollList";
import { useEffect, useState } from "react";
import { useAtom } from "jotai";
import { searchTermAtom } from "@/libs/polls/atoms/searchTermAtom";

interface Props {
  initialPolls: { poll: PollSimpleView; user: UserQueryView | null }[];
}

export default function Home({ initialPolls }: Props) {
  const [polls, setPolls] = useState(initialPolls);
  const [searchTerm] = useAtom(searchTermAtom);
  const [loading, setLoading] = useState(false);
  

  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const pageSize = 10;

  useEffect(() => {
    const checkAuth = async () => {
      await getCurrentUser();
    };
    
    checkAuth();
  }, []);

  useEffect(() => {
    setCurrentPage(0);
    handleSearch(0);
  }, [searchTerm]);

  const handleSearch = async (page: number) => {
    try {
      setLoading(true);
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
      }
    } catch (error) {
      console.error("Error searching polls:", error);
    } finally {
      setLoading(false);
    }
  };

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
    handleSearch(page);
  };

  const Pagination = ({ currentPage, totalPages, onPageChange }: { currentPage: number, totalPages: number, onPageChange: (page: number) => void }) => {
    if (totalPages <= 1) return null;
    
    return (
      <div className="flex justify-center mt-4 space-x-2">
        <button 
          onClick={() => onPageChange(Math.max(0, currentPage - 1))}
          disabled={currentPage === 0}
          className={`px-3 py-1 rounded ${currentPage === 0 ? 'bg-gray-200 text-gray-500 cursor-not-allowed' : 'bg-blue-500 text-white hover:bg-blue-600 cursor-pointer'}`}
        >
          Anterior
        </button>
        
        <span className="px-3 py-1">
          Página {currentPage + 1} de {totalPages}
        </span>
        
        <button 
          onClick={() => onPageChange(Math.min(totalPages - 1, currentPage + 1))}
          disabled={currentPage >= totalPages - 1}
          className={`px-3 py-1 rounded ${currentPage >= totalPages - 1 ? 'bg-gray-200 text-gray-500 cursor-not-allowed' : 'bg-blue-500 text-white hover:bg-blue-600 cursor-pointer'}`}
        >
          Próximo
        </button>
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
    const response = await searchPollsByTitle("", 0, 10);
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
