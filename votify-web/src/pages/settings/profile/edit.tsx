import { NextPage, GetServerSideProps } from 'next';
import Head from 'next/head';
import { useRouter } from 'next/router';
import { useState, useEffect } from 'react';

interface ApiUser {
  id: number;
  name: string;
  userName: string;
  email?: string | null;
  role?: string | null;
}

interface EditProfilePageProps {
  authenticatedUser: ApiUser | null;
  error?: string;
}

const EditProfilePage: NextPage<EditProfilePageProps> = ({ authenticatedUser, error }) => {
  const router = useRouter();
  const [name, setName] = useState(authenticatedUser?.name || '');
  const [userName, setUserName] = useState(authenticatedUser?.userName || '');
  const [formError, setFormError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  useEffect(() => {
    if (authenticatedUser) {
      setName(authenticatedUser.name);
      setUserName(authenticatedUser.userName);
    }
  }, [authenticatedUser]);

  if (error || !authenticatedUser) {
    return (
      <div className="container mx-auto p-4 text-center">
        <Head><title>Erro - Votify</title></Head>
        <h1 className="text-2xl font-bold text-red-600">Acesso Negado</h1>
        <p className="text-gray-700">{error || 'Você precisa estar logado para editar o perfil.'}</p>
        <button onClick={() => router.push('/login')} className="mt-4 bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded">
          Ir para Login
        </button>
      </div>
    );
  }

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setFormError(null);
    setSuccessMessage(null);

    const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8081'; 

    try {
      const response = await fetch(`${API_BASE_URL}/users/me/info`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ name, userName }),
        credentials: 'include',
      });

      const result = await response.json();

      if (!response.ok) {
        if (response.status === 401) {
            setFormError(result.errorMessage || 'Sessão expirada ou inválida. Por favor, faça login novamente.');
        } else {
            setFormError(result.errorMessage || 'Falha ao atualizar o perfil. Verifique os dados e tente novamente.');
        }
        return;
      }

      setSuccessMessage('Perfil atualizado com sucesso!');
    } catch (err) {
      console.error("Erro ao submeter formulário de edição:", err);
      setFormError('Ocorreu um erro de rede. Tente novamente mais tarde.');
    }
  };

  return (
    <div className="container mx-auto p-4 max-w-lg">
      <Head>
        <title>Editar Perfil - Votify</title>
      </Head>
      <h1 className="text-3xl font-bold text-gray-800 mb-6">Editar Informações do Perfil</h1>
      
      <form onSubmit={handleSubmit} className="bg-white shadow-md rounded-lg p-6 space-y-4">
        {formError && <p className="text-red-500 text-sm bg-red-100 p-3 rounded">{formError}</p>}
        {successMessage && <p className="text-green-500 text-sm bg-green-100 p-3 rounded">{successMessage}</p>}
        
        <div>
          <label htmlFor="name" className="block text-sm font-medium text-gray-700">Nome:</label>
          <input 
            type="text" 
            id="name" 
            value={name} 
            onChange={(e) => setName(e.target.value)} 
            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
            required 
          />
        </div>
        
        <div>
          <label htmlFor="userName" className="block text-sm font-medium text-gray-700">Nome de Usuário:</label>
          <input 
            type="text" 
            id="userName" 
            value={userName} 
            onChange={(e) => setUserName(e.target.value)} 
            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
            required 
            minLength={3}
          />
        </div>
        
        <button 
          type="submit" 
          className="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
        >
          Salvar Alterações
        </button>
      </form>
    </div>
  );
};

export const getServerSideProps: GetServerSideProps = async (context) => {
  const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8081';
  let authenticatedUser: ApiUser | null = null;

  try {
    const authCheckUrl = `${API_BASE_URL}/users/me`;
    const authCheckRes = await fetch(authCheckUrl, {
      headers: { cookie: context.req.headers.cookie || '' },
    });

    if (!authCheckRes.ok) {
      return { redirect: { destination: '/login?reason=unauthenticated', permanent: false } };
    }
    const authApiResponse = await authCheckRes.json();
    if (authApiResponse.success && authApiResponse.data) {
      authenticatedUser = authApiResponse.data;
    } else {
      return { redirect: { destination: '/login?reason=auth_data_invalid', permanent: false } };
    }
  } catch (error) {
    console.error('[EditProfilePage] Erro ao verificar autenticação:', error);
    return { redirect: { destination: '/login?reason=auth_check_error', permanent: false } };
  }

  if (!authenticatedUser) {
    return { redirect: { destination: '/login?reason=no_auth_user_prop', permanent: false } };
  }

  return {
    props: {
      authenticatedUser,
    },
  };
};

export default EditProfilePage; 