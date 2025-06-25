import PollSimpleView from "@/libs/polls/PollSimpleView";
import { GetServerSideProps } from "next";
import UserQueryView from "@/libs/users/UserQueryView";
import PollList from "@/components/shared/PollList";
import { useRouter } from "next/router";
import Pagination from "./components/Pagination";
import Head from "next/head";
import { getAllActivePolls, getUserById } from "@/libs/api";

interface Props {
    polls: { poll: PollSimpleView; user: UserQueryView | null }[];
    page: number;
    totalPages: number;
}

const pageSize = 10;

export default function Home({ polls, page, totalPages }: Props) {
    const router = useRouter();

    const handlePageChange = (page: number) => {
        router.push(
            {
                pathname: "/home",
                query: {
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
                <title>Home - Votify</title>
            </Head>
            <div className="flex flex-col gap-4">
                <div className="flex flex-col gap-3">
                    <PollList polls={polls} />
                    <Pagination
                        currentPage={page}
                        totalPages={totalPages}
                        onPageChange={handlePageChange}
                    />
                </div>
            </div>
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