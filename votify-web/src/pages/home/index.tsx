import PollSimpleView from "@/libs/polls/PollSimpleView";
import { GetServerSideProps } from "next";
import UserQueryView from "@/libs/users/UserQueryView";
import PollList from "@/components/shared/PollList";
import { useRouter } from "next/router";
import Pagination from "./components/Pagination";
import Head from "next/head";
import { getAllActivePolls, getUserById } from "@/libs/api";
import { useState } from "react";
import CreatePollModal from "@/components/polls/CreatePollModal";
import Button from "@/components/shared/Button";
import { useAtomValue } from "jotai";
import { currentUserAtom } from "@/libs/users/atoms/currentUserAtom";

interface Props {
  polls: { poll: PollSimpleView; user: UserQueryView | null }[];
  page: number;
  totalPages: number;
}

const pageSize = 10;

export default function Home({ polls, page, totalPages }: Props) {
  const router = useRouter();
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const currentUser = useAtomValue(currentUserAtom);

  const handlePageChange = (page: number) => {
    router.push(`/home?page=${page}`);
  };

  return (
    <>
      <Head>
        <title>Home - Votify</title>
      </Head>
      <div className="flex flex-col gap-4">
        <div className="flex justify-end">
          <Button
            id="open-poll-create-modal"
            scheme="primary"
            onClick={() => setIsCreateModalOpen(true)}
            disabled={!currentUser}
            title={
              !currentUser
                ? "Você precisa estar logado para criar uma enquete"
                : undefined
            }
          >
            Criar Nova Enquete
          </Button>
        </div>
        <div className="flex flex-col gap-3">
          <PollList polls={polls} />
          <Pagination
            currentPage={page}
            totalPages={totalPages}
            onPageChange={handlePageChange}
          />
        </div>
      </div>

      <CreatePollModal
        isOpen={isCreateModalOpen}
        onClose={() => setIsCreateModalOpen(false)}
      />
    </>
  );
}

export const getServerSideProps: GetServerSideProps<Props> = async (
  context,
) => {
  const { page } = context.query;
  const pageNumber = typeof page === "string" ? parseInt(page, 10) : 0;

  const response = await getAllActivePolls(pageNumber, pageSize);

  if (!response.success) {
    return {
      props: {
        polls: [],
        page: pageNumber,
        totalPages: 1,
        errorMessage: "Não foi possível acesar as enquetes ativas",
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
      page: response.data?.pageNumber || pageNumber,
      totalPages: response.data?.totalPages || 1,
    },
  };
};
