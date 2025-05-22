import React, { useState } from "react";
import { useRouter } from "next/router";
import { deleteCurrentUser } from "@/libs/api";
import Button from "@/components/shared/Button";

const UserSettings: React.FC = () => {
  const router = useRouter();
  const [deleteError, setDeleteError] = useState<string | null>(null);
  const [isDeleting, setIsDeleting] = useState<boolean>(false);

  const handleEditProfile = () => {
    router.push("/settings/profile/edit");
  };

  const handleDeleteAccount = async () => {
    setDeleteError(null);
    const confirmationMessage =
      "Tem certeza que deseja deletar sua conta? Esta ação é irreversível e todos os seus dados associados (como enquetes criadas) serão perdidos.";
    if (!window.confirm(confirmationMessage)) return;

    setIsDeleting(true);
    const response = await deleteCurrentUser();
    if (response.success) {
      alert("Sua conta foi deletada com sucesso.");
      router.push("/home");
    }
    setDeleteError("Não foi possível deletar a conta.");
  };

  return (
    <div className="space-y-4">
      <div>
        <h3 className="text-xl font-medium mb-2">Ações da Conta</h3>
        <div className="flex flex-col items-center gap-3">
          <Button
            onClick={handleEditProfile}
            scheme="primary"
            disabled={isDeleting}
          >
            Editar Informações do Usuário
          </Button>
          <Button
            onClick={handleDeleteAccount}
            scheme="red"
            variant="text"
            disabled={isDeleting}
          >
            {isDeleting ? "Deletando..." : "Deletar Conta"}
          </Button>
        </div>
        {deleteError && (
          <p className="text-red-500 text-sm mt-2 bg-red-100 p-3 rounded">
            {deleteError}
          </p>
        )}
      </div>
    </div>
  );
};

export default UserSettings;
