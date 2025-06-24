import React, { useState } from "react";
import { useRouter } from "next/router";
import { deleteCurrentUser } from "@/libs/api";
import Button from "@/components/shared/Button";
import Modal from "@/components/shared/Modal";
import { useSetAtom } from "jotai";
import { currentUserAtom } from "@/libs/users/atoms/currentUserAtom";

const UserSettings: React.FC = () => {
  const router = useRouter();
  const setCurrentUser = useSetAtom(currentUserAtom);
  const [deleteError, setDeleteError] = useState<string | null>(null);
  const [isDeleting, setIsDeleting] = useState<boolean>(false);

  const handleEditProfile = () => {
    router.push("/settings");
  };

  const handleDeleteAccount = async () => {
    setDeleteError(null);
    const response = await deleteCurrentUser();
    if (response.success) {
      setCurrentUser(null);
      router.push("/home");
      return;
    }
    setDeleteError("Não foi possível deletar a conta.");
  };

  return (
    <>
      <Modal isOpen={isDeleting} onClose={() => setIsDeleting(false)}>
        <h2 className="text-3xl">Tem certeza que deseja deletar sua conta?</h2>
        <p>
          Esta ação é irreversível e a maior parte dos seus dados associados
          (como enquetes criadas ainda ativas) serão perdidos.
        </p>
        {deleteError && (
          <p className="text-red-500 text-sm mt-2 bg-red-100 p-3 rounded">
            {deleteError}
          </p>
        )}
        <Button
          id="confirm-delete-button"
          onClick={handleDeleteAccount}
          scheme="red"
        >
          Eu entendo que essa ação é irreversível. Desejo remover minha conta
        </Button>
      </Modal>
      <div className="space-y-4">
        <div>
          <h3 className="text-xl font-medium mb-2">Ações da Conta</h3>
          <div className="flex flex-col items-center gap-3">
            <Button
              onClick={handleEditProfile}
              scheme="primary"
              disabled={isDeleting}
              id="edit-profile-button"
            >
              Editar Informações do Usuário
            </Button>
            <Button
              onClick={() => setIsDeleting(true)}
              scheme="red"
              variant="text"
              disabled={isDeleting}
              id="delete-account-button"
            >
              {isDeleting ? "Deletando..." : "Deletar Conta"}
            </Button>
          </div>
        </div>
      </div>
    </>
  );
};

export default UserSettings;
