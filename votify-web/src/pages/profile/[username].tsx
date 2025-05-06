import UserProfile from '@/components/profile/UserProfile';
import Head from 'next/head';
import type { GetServerSideProps, NextPage } from 'next';

interface ApiUser {
  id: number;
  name: string;
  userName: string;
  email?: string | null;
  role?: string | null; 
}

interface ApiPoll {
  id: number;
  title: string;
  description?: string | null;
  startDate?: string | null;
  endDate?: string | null;
  responsibleId?: number | null;
}

interface ProfilePageProps {
  profileUserData: ApiUser | null;
  userPolls: ApiPoll[];
  authenticatedUserData?: ApiUser | null;
  error?: string;
}

const ProfilePage: NextPage<ProfilePageProps> = ({ profileUserData, userPolls, authenticatedUserData, error }) => {
  if (error || !profileUserData) {
    return (
      <>
        <Head>
          <title>Erro - Votify</title>
        </Head>
        <div className="container mx-auto p-4 text-center">
          <h1 className="text-2xl font-bold text-red-600">Erro ao carregar perfil</h1>
          <p className="text-gray-700">{error || 'Usuário não encontrado ou ocorreu um problema.'}</p>
        </div>
      </>
    );
  }

  const isAuthenticatedUserProfile = !!authenticatedUserData && authenticatedUserData.userName === profileUserData.userName;

  const userProfileComponentData = {
    id: profileUserData.id,
    name: profileUserData.name,
    username: profileUserData.userName, 
    polls: userPolls,
  };

  return (
    <>
      <Head>
        <title>{userProfileComponentData.name} (@{userProfileComponentData.username}) - Perfil Votify</title>
      </Head>
      <UserProfile user={userProfileComponentData} isAuthenticatedUserProfile={isAuthenticatedUserProfile} />
    </>
  );
};

export const getServerSideProps: GetServerSideProps = async (context) => {
  const { username: routeUsername } = context.params as { username: string };
  const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8081'; 

  let sspAuthenticatedUserData: ApiUser | null = null;

  try {
    const authCheckUrl = `${API_BASE_URL}/users/me`;
    const authCheckRes = await fetch(authCheckUrl, {
      headers: { cookie: context.req.headers.cookie || '' },
    });

    if (!authCheckRes.ok) {
      return { redirect: { destination: '/login', permanent: false } };
    }
    const authApiResponse = await authCheckRes.json();
    if (authApiResponse.success && authApiResponse.data) {
      sspAuthenticatedUserData = authApiResponse.data;
    } else {
      return { redirect: { destination: '/login?error=auth_data_missing', permanent: false } };
    }
  } catch (authError) {
    return { redirect: { destination: '/login?error=auth_check_failed', permanent: false } };
  }

  try {
    const userApiUrl = `${API_BASE_URL}/users/username/${routeUsername}`;
    const userRes = await fetch(userApiUrl);

    if (!userRes.ok) {
      if (userRes.status === 404) {
        return { notFound: true };
      }
      return { props: { profileUserData: null, userPolls: [], authenticatedUserData: sspAuthenticatedUserData, error: `Falha ao buscar perfil (${userRes.status}).` } };
    }

    const userApiResponse = await userRes.json();
    const rawProfileUserData: ApiUser = userApiResponse.data;

    if (!rawProfileUserData || typeof rawProfileUserData.id === 'undefined') {
      return { props: { profileUserData: null, userPolls: [], authenticatedUserData: sspAuthenticatedUserData, error: 'Resposta da API para perfil inválida.' } };
    }

    const profileUserData: ApiUser = {
        id: rawProfileUserData.id,
        name: rawProfileUserData.name,
        userName: rawProfileUserData.userName,
        email: rawProfileUserData.email,
        role: rawProfileUserData.role
    };

    let userPolls: ApiPoll[] = [];
    const pollsApiUrl = `${API_BASE_URL}/polls/user/${profileUserData.id}?page=0&size=10`;
    const pollsRes = await fetch(pollsApiUrl);
    if (pollsRes.ok) {
        const pollsApiResponse = await pollsRes.json();
        userPolls = (pollsApiResponse.data?.content || []).map((poll: any) => ({
          id: poll.id, title: poll.title, description: poll.description,
          startDate: poll.startDate, endDate: poll.endDate, responsibleId: poll.responsibleId,
        }));
    }
    
    return {
      props: {
        profileUserData,
        userPolls,
        authenticatedUserData: sspAuthenticatedUserData,
      },
    };
  } catch (err) {
    const errorMessage = err instanceof Error ? err.message : 'Erro inesperado.';
    return { props: { profileUserData: null, userPolls: [], authenticatedUserData: sspAuthenticatedUserData, error: errorMessage } };
  }
};

export default ProfilePage; 