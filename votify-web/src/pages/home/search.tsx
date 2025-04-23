import PollSimpleView from "@/libs/polls/PollSimpleView";
import { getUserById, searchPollsByTitle } from "@/libs/api";
import { GetServerSideProps } from "next";
import UserQueryView from "@/libs/users/UserQueryView";
import PollList from "./components/PollList";
import { useEffect, useState } from "react";
import { useAtom } from "jotai";
import { searchTermAtom } from "@/libs/polls/atoms/searchTermAtom";
import Button from "@/components/shared/Button";
import { useRouter } from "next/router";

interface Props {
  initialPolls: { poll: PollSimpleView; user: UserQueryView | null }[];
  searchTitle: string;
  initialPage: number; 
  initialTotalPages: number;
  errorMessage?: string;
}

export default function SearchResults({ initialPolls, searchTitle, initialPage, initialTotalPages, errorMessage }: Props) {
  const router = useRouter();
  const [, setSearchTerm] = useAtom(searchTermAtom);
  const [polls, setPolls] = useState(initialPolls);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(errorMessage || null);
  const [currentPage, setCurrentPage] = useState(initialPage);
  const [totalPages, setTotalPages] = useState(initialTotalPages);
  const pageSize = 10;
  
  useEffect(() => {
    setSearchTerm(searchTitle);
  }, [searchTitle, setSearchTerm]);
  
  useEffect(() => {
    const titleParam = router.query.title as string;
    const pageParam = router.query.page;
    
    if (titleParam && titleParam.trim() !== "") {
      const newPage = typeof pageParam === 'string' ? parseInt(pageParam, 10) : 0;
      setCurrentPage(newPage);
      fetchSearchResults(titleParam, newPage);
    }
  }, [router.query.title, router.query.page]);
  
  const fetchSearchResults = async (title: string, page: number) => {
    setLoading(true);
    setError(null);
    
    try {
      const response = await searchPollsByTitle(title, page, pageSize);
      
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
        setError(`Erro na busca: ${response.errorMessage}`);
        setPolls([]);
      }
    } catch (error) {
      console.error("Error searching polls:", error);
      setError("Ocorreu um erro ao buscar enquetes. Por favor, tente novamente.");
      setPolls([]);
    } finally {
      setLoading(false);
    }
  };

  const handlePageChange = (page: number) => {
    router.push({
      pathname: '/home/search',
      query: { 
        title: searchTitle,
        page: page 
      }
    }, undefined, { shallow: false });
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
          {`Resultados da busca por "${searchTitle}"`}
        </h2>
        {loading ? (
          <p>Carregando...</p>
        ) : error ? (
          <div className="p-4 bg-red-100 text-red-800 rounded-md">
            {error}
          </div>
        ) : polls.length === 0 ? (
          <p>{`Nenhuma enquete encontrada para "${searchTitle}"`}</p>
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

export const getServerSideProps: GetServerSideProps<Props> = async (context) => {
  const { title, page } = context.query;
  const searchTitle = typeof title === 'string' ? title : '';
  const pageNumber = typeof page === 'string' ? parseInt(page, 10) : 0;
  
  // Check if search term is empty and redirect to home if it is
  if (!searchTitle || searchTitle.trim() === "" || searchTitle.trim() === " ") {
    return {
      redirect: {
        destination: '/home',
        permanent: false,
      },
    };
  }

  try {
    const response = await searchPollsByTitle(searchTitle, pageNumber, 10);
    
    if (!response.success) {
      return {
        props: {
          initialPolls: [],
          searchTitle: searchTitle,
          initialPage: pageNumber,
          initialTotalPages: 1,
          errorMessage: response.errorMessage || "Erro na busca",
        },
      };
    }
    
    let pollsWithUserData: { poll: PollSimpleView; user: UserQueryView | null }[] = [];
    
    if (response.data && response.data.content) {
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
        searchTitle: searchTitle,
        initialPage: response.data?.pageNumber || pageNumber,
        initialTotalPages: response.data?.totalPages || 1,
      },
    };
  } catch (error) {
    console.error("Error fetching search results:", error);
    return {
      props: {
        initialPolls: [],
        searchTitle: searchTitle,
        initialPage: pageNumber,
        initialTotalPages: 1,
        errorMessage: "Ocorreu um erro ao buscar enquetes. Por favor, tente novamente.",
      },
    };
  }
}; 