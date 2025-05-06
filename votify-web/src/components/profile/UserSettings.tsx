import React, { useState } from 'react';
import { useRouter } from 'next/router';

const UserSettings: React.FC = () => {
  const router = useRouter();
  const [deleteError, setDeleteError] = useState<string | null>(null);
  const [isDeleting, setIsDeleting] = useState<boolean>(false);

  const handleEditProfile = () => {
    router.push('/settings/profile/edit');
  };

  const handleDeleteAccount = async () => {
    setDeleteError(null);
    const confirmationMessage = "Tem certeza que deseja deletar sua conta? Esta ação é irreversível e todos os seus dados associados (como enquetes criadas) serão perdidos.";
    if (window.confirm(confirmationMessage)) {
      setIsDeleting(true);
      const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8081';
      try {
        const response = await fetch(`${API_BASE_URL}/users/me`, {
          method: 'DELETE',
          credentials: 'include',
          headers: {
          },
        });

        if (!response.ok) {
          let errorMessage = 'Falha ao deletar a conta.';
          try {
            const result = await response.json();
            errorMessage = result.errorMessage || errorMessage;
          } catch (e) {
          }
          if (response.status === 401) {
            setDeleteError('Sua sessão expirou ou é inválida. Por favor, faça login novamente para deletar a conta.');
          } else {
            setDeleteError(`${errorMessage} (Status: ${response.status})`);
          }
          setIsDeleting(false);
          return;
        }

        alert('Sua conta foi deletada com sucesso.');
        router.push('/login?deleted=true');

      } catch (err) {
        console.error("Erro ao deletar conta:", err);
        setDeleteError('Ocorreu um erro de rede ao tentar deletar a conta. Tente novamente mais tarde.');
        setIsDeleting(false);
      }
    }
  };

  return (
    <div className="space-y-4">
      <div>
        <h3 className="text-xl font-medium text-gray-700 mb-2">Ações da Conta</h3>
        <button
          onClick={handleEditProfile}
          className="w-full sm:w-auto bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline mb-2 sm:mb-0 sm:mr-2"
          disabled={isDeleting}
        >
          Editar Informações do Usuário
        </button>
        <button
          onClick={handleDeleteAccount}
          className={`w-full sm:w-auto text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline ${
            isDeleting ? 'bg-gray-400 cursor-not-allowed' : 'bg-red-500 hover:bg-red-600'
          }`}
          disabled={isDeleting}
        >
          {isDeleting ? 'Deletando...' : 'Deletar Conta'}
        </button>
        {deleteError && <p className="text-red-500 text-sm mt-2 bg-red-100 p-3 rounded">{deleteError}</p>}
      </div>
    </div>
  );
};

export default UserSettings; 