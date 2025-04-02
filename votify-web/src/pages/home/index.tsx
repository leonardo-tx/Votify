import PollSimpleView from "@/libs/polls/PollSimpleView";
import { getUserById } from "@/libs/api";
import { GetServerSideProps } from "next";
import UserQueryView from "@/libs/users/UserQueryView";
import PollList from "./components/PollList";

interface Props {
  polls: { poll: PollSimpleView; user: UserQueryView | null }[];
}

export default function Home({ polls }: Props) {
  return (
    <div className="flex flex-col gap-4">
      <h1 className="text-xl font-extrabold">
        Nenhuma das enquetes aqui são reais, falta implementação no back-end!
      </h1>
      <PollList polls={polls} />
    </div>
  );
}

export const getServerSideProps: GetServerSideProps<Props> = async () => {
  const polls: PollSimpleView[] = [
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
  const users: Map<number, UserQueryView> = new Map();
  const pollsWithUserData: {
    poll: PollSimpleView;
    user: UserQueryView | null;
  }[] = [];
  for (let i = 0; i < polls.length; i++) {
    const poll = polls[i];
    const userFromMap = users.get(poll.responsibleId);
    if (userFromMap !== undefined) {
      pollsWithUserData.push({ poll, user: userFromMap });
      continue;
    }

    try {
      const user = (await getUserById(poll.responsibleId)).data;

      pollsWithUserData.push({ poll, user: user ?? null });
      users.set(user!.id, user!);
    } catch {
      pollsWithUserData.push({ poll, user: null });
    }
  }

  return {
    props: {
      polls: pollsWithUserData,
    },
  };
};
