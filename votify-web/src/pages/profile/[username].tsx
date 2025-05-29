import UserProfile from "@/pages/profile/components/UserProfile";
import Head from "next/head";
import type { GetServerSideProps } from "next";
import { getPollsFromUser, getUserByUserName } from "@/libs/api";
import { useAtomValue } from "jotai";
import { currentUserAtom } from "@/libs/users/atoms/currentUserAtom";
import UserQueryView from "@/libs/users/UserQueryView";
import VotifyErrorCode from "@/libs/VotifyErrorCode";
import PollSimpleView from "@/libs/polls/PollSimpleView";

interface ProfilePageProps {
  profileUserData: UserQueryView | null;
  userPolls: PollSimpleView[];
  error?: string;
}

export default function ProfilePage({
  profileUserData,
  userPolls,
  error,
}: ProfilePageProps) {
  const currentUser = useAtomValue(currentUserAtom);

  if (error || profileUserData === null) {
    return (
      <>
        <Head>
          <title>Erro - Votify</title>
        </Head>
        <div className="container mx-auto p-4 text-center">
          <h1
            id="profile-error-title"
            className="text-2xl font-bold text-red-600"
          >
            Erro ao carregar perfil
          </h1>
          <p id="profile-error-message" className="text-gray-700">
            {error || "Usuário não encontrado ou ocorreu um problema."}
          </p>
        </div>
      </>
    );
  }

  const isAuthenticatedUserProfile = currentUser?.id === profileUserData.id;
  const userProfileComponentData = {
    id: profileUserData.id,
    name: profileUserData.name,
    username: profileUserData.userName,
    polls: userPolls,
  };

  return (
    <>
      <Head>
        <title>
          {userProfileComponentData.name} (@{userProfileComponentData.username})
          - Perfil Votify
        </title>
      </Head>
      <UserProfile
        user={userProfileComponentData}
        isAuthenticatedUserProfile={isAuthenticatedUserProfile}
      />
    </>
  );
}

export const getServerSideProps: GetServerSideProps<ProfilePageProps> = async (
  context,
) => {
  const { username: routeUsername } = context.params as { username: string };

  const userResponse = await getUserByUserName(
    routeUsername,
    context.req.headers.cookie,
  );
  if (!userResponse.success) {
    return {
      props: {
        profileUserData: null,
        userPolls: [],
        error:
          userResponse.errorCode === VotifyErrorCode.USER_NOT_FOUND
            ? "Perfil não encontrado."
            : "Falha ao buscar perfil.",
      },
    };
  }

  const pollResponse = await getPollsFromUser(userResponse.data!.id);
  return {
    props: {
      profileUserData: userResponse.data,
      userPolls: pollResponse.data!.content,
    },
  };
};
