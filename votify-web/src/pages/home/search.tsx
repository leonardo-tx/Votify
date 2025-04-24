import PollSimpleView from "@/libs/polls/PollSimpleView";
import { getUserById, searchPollsByTitle } from "@/libs/api";
import { GetServerSideProps } from "next";
import UserQueryView from "@/libs/users/UserQueryView";
import PollList from "./components/PollList";
import { useRouter } from "next/router";
import Pagination from "./components/Pagination";
import Head from "next/head";

interface Props {
  polls: { poll: PollSimpleView; user: UserQueryView | null }[];
  searchTitle: string;
  page: number;
  totalPages: number;
  errorMessage?: string;
}

const pageSize = 10;

export default function SearchResults({
  polls,
  searchTitle,
  page,
  totalPages,
  errorMessage,
}: Props) {
  const router = useRouter();
  const handlePageChange = (page: number) => {
    router.push(
      {
        pathname: "/home/search",
        query: {
          title: searchTitle,
          page: page,
        },
      },
      undefined,
      { shallow: false },
    );
  };

  return (
    <>
      <Head>
        <title>Pesquisa - Votify</title>
      </Head>
      <div className="flex flex-col gap-4">
        <div>
          <h2 className="text-xl font-bold mb-2">
            {`Resultados da busca por "${searchTitle}"`}
          </h2>
          {polls.length === 0 ? (
            <p>
              {errorMessage ||
                `Nenhuma enquete encontrada para "${searchTitle}"`}
            </p>
          ) : (
            <>
              <PollList polls={polls} />
              <Pagination
                currentPage={page}
                totalPages={totalPages}
                onPageChange={handlePageChange}
              />
            </>
          )}
        </div>
      </div>
    </>
  );
}

export const getServerSideProps: GetServerSideProps<Props> = async (
  context,
) => {
  const { title, page } = context.query;
  const searchTitle = typeof title === "string" ? title : "";
  const pageNumber = typeof page === "string" ? parseInt(page, 10) : 0;

  if (searchTitle.trim() === "") {
    return {
      redirect: {
        destination: "/home",
        permanent: false,
      },
    };
  }

  const response = await searchPollsByTitle(searchTitle, pageNumber, pageSize);

  if (!response.success) {
    return {
      props: {
        polls: [],
        searchTitle: searchTitle,
        page: pageNumber,
        totalPages: 1,
        errorMessage: "Não foi possível fazer a busca",
      },
    };
  }

  let pollsWithUserData: {
    poll: PollSimpleView;
    user: UserQueryView | null;
  }[] = [];

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
      }),
    );
  }

  return {
    props: {
      polls: pollsWithUserData,
      searchTitle: searchTitle,
      page: response.data?.pageNumber || pageNumber,
      totalPages: response.data?.totalPages || 1,
    },
  };
};
