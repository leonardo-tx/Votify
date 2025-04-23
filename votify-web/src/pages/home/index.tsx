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
import { useRouter } from "next/router";

const mockPolls: PollSimpleView[] = [
  {
    id: 1,
    title: "Lorem Ipsum",
    description: "",
    startDate: "2025-02-01T00:00:00",
    endDate: "2025-04-12T02:20:00",
    responsibleId: 1,
  },
  {
    id: 2,
    title: "Esse é o máximo de letras que um título pode ter..",
    description: "asdhka",
    startDate: "2025-02-01T00:00:00",
    endDate: "2025-08-01T00:00:00",
    responsibleId: 1,
  },
  {
    id: 3,
    title: "Outro teste",
    description: "asdhka",
    startDate: "2025-02-01T00:00:00",
    endDate: "2025-08-01T00:00:00",
    responsibleId: 1,
  },
  {
    id: 4,
    title: "Uma enquete bem específica",
    description: "hahaha",
    startDate: "2025-02-01T00:00:00",
    endDate: "2025-08-01T00:00:00",
    responsibleId: 1,
  },
  {
    id: 5,
    title: "?????????",
    description: "Abra e descubra",
    startDate: "2025-02-01T00:00:00",
    endDate: "2025-08-01T00:00:00",
    responsibleId: 1,
  },
  {
    id: 6,
    title: "Não sei o que digitar",
    description: "",
    startDate: "2025-02-01T00:00:00",
    endDate: "2025-08-01T00:00:00",
    responsibleId: 1,
  },
  {
    id: 7,
    title: "Lorem Ipsum",
    description: "",
    startDate: "2025-02-01T00:00:00",
    endDate: "2025-08-01T00:00:00",
    responsibleId: 1,
  },
  {
    id: 8,
    title: "Esse é o máximo de letras que um título pode ter..",
    description: "asdhka",
    startDate: "2025-02-01T00:00:00",
    endDate: "2025-08-01T00:00:00",
    responsibleId: 1,
  },
  {
    id: 9,
    title: "Outro teste",
    description: "asdhka",
    startDate: "2025-02-01T00:00:00",
    endDate: "2025-08-01T00:00:00",
    responsibleId: 1,
  },
  {
    id: 10,
    title: "Uma enquete bem específica",
    description: "hahaha",
    startDate: "2025-02-01T00:00:00",
    endDate: "2025-08-01T00:00:00",
    responsibleId: 1,
  },
  {
    id: 11,
    title: "?????????",
    description: "Abra e descubra",
    startDate: "2025-02-01T00:00:00",
    endDate: "2025-08-01T00:00:00",
    responsibleId: 1,
  },
  {
    id: 12,
    title: "Não sei o que digitar",
    description: "",
    startDate: "2025-02-01T00:00:00",
    endDate: "2025-08-01T00:00:00",
    responsibleId: 1,
  },
  {
    id: 13,
    title: "Lorem Ipsum",
    description: "",
    startDate: "2025-02-01T00:00:00",
    endDate: "2025-08-01T00:00:00",
    responsibleId: 1,
  },
  {
    id: 14,
    title: "Esse é o máximo de letras que um título pode ter..",
    description: "asdhka",
    startDate: "2025-02-01T00:00:00",
    endDate: "2025-08-01T00:00:00",
    responsibleId: 1,
  },
  {
    id: 15,
    title: "Outro teste",
    description: "asdhka",
    startDate: "2025-02-01T00:00:00",
    endDate: "2025-08-01T00:00:00",
    responsibleId: 1,
  },
  {
    id: 16,
    title: "Uma enquete bem específica",
    description: "hahaha",
    startDate: "2025-02-01T00:00:00",
    endDate: "2025-08-01T00:00:00",
    responsibleId: 1,
  },
  {
    id: 17,
    title: "?????????",
    description: "Abra e descubra",
    startDate: "2025-02-01T00:00:00",
    endDate: "2025-08-01T00:00:00",
    responsibleId: 1,
  },
  {
    id: 18,
    title: "Não sei o que digitar",
    description: "",
    startDate: "2025-02-01T00:00:00",
    endDate: "2025-08-01T00:00:00",
    responsibleId: 1,
  },
  {
    id: 19,
    title: "Lorem Ipsum",
    description: "",
    startDate: "2025-02-01T00:00:00",
    endDate: "2025-08-01T00:00:00",
    responsibleId: 1,
  },
  {
    id: 20,
    title: "Esse é o máximo de letras que um título pode ter..",
    description: "asdhka",
    startDate: "2025-02-01T00:00:00",
    endDate: "2025-08-01T00:00:00",
    responsibleId: 1,
  },
  {
    id: 21,
    title: "Outro teste",
    description: "asdhka",
    startDate: "2025-02-01T00:00:00",
    endDate: "2025-08-01T00:00:00",
    responsibleId: 1,
  },
  {
    id: 22,
    title: "Uma enquete bem específica",
    description: "hahaha",
    startDate: "2025-02-01T00:00:00",
    endDate: "2025-08-01T00:00:00",
    responsibleId: 1,
  },
  {
    id: 23,
    title: "?????????",
    description: "Abra e descubra",
    startDate: "2025-02-01T00:00:00",
    endDate: "2025-08-01T00:00:00",
    responsibleId: 1,
  },
  {
    id: 24,
    title: "Não sei o que digitar",
    description: "",
    startDate: "2025-02-01T00:00:00",
    endDate: "2025-08-01T00:00:00",
    responsibleId: 1,
  },
];

interface Props {
  initialPolls: { poll: PollSimpleView; user: UserQueryView | null }[];
}

export default function Home({ initialPolls }: Props) {
  const router = useRouter();
  const [polls, setPolls] = useState(initialPolls);
  const [searchTerm] = useAtom(searchTermAtom);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const pageSize = 10;

  useEffect(() => {
    if (searchTerm && searchTerm.trim() !== "" && searchTerm.trim() !== " ") {
      router.push({
        pathname: '/home/search',
        query: { 
          title: searchTerm.trim(),
          page: 0 
        }
      });
      return;
    }
    
    setCurrentPage(0);
    handleSearch(0);
  }, [searchTerm, router]);

  const handleSearch = async (page: number) => {
    setLoading(true);
    setError(null);
    const query = searchTerm?.trim() || "";
    
    if (query === "" || query === " ") {
      const startIndex = page * pageSize;
      const endIndex = startIndex + pageSize;
      const paginatedMockPolls = mockPolls.slice(startIndex, endIndex);
      
      setPolls(paginatedMockPolls.map(poll => ({ poll, user: null })));
      setCurrentPage(page);
      setTotalPages(Math.ceil(mockPolls.length / pageSize));
      setLoading(false);
      return;
    }
    
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
        setPolls(mockPolls.slice(0, pageSize).map(poll => ({ poll, user: null })));
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
    const initialPage = 0;
    const pageSize = 10;
    const paginatedMockPolls = mockPolls.slice(initialPage * pageSize, (initialPage + 1) * pageSize);

    const pollsWithUserData = paginatedMockPolls.map(poll => ({
      poll,
      user: null
    }));

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
