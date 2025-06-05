import Button from "@/components/shared/Button";
import Input from "@/components/shared/Input";
import { updateUserInfo } from "@/libs/api";
import { currentUserAtom } from "@/libs/users/atoms/currentUserAtom";
import UserUpdateInfoDTO from "@/libs/users/UserUpdateInfoDTO";
import { useAtom } from "jotai";
import { useState, useEffect } from "react";

export default function UserInfoForm() {
  const [currentUser, setCurrentUser] = useAtom(currentUserAtom);
  const [userInfo, setUserInfo] = useState<UserUpdateInfoDTO>({
    name: "",
    userName: "",
  });
  const [formError, setFormError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  useEffect(() => {
    if (currentUser !== null) {
      setUserInfo({ name: currentUser.name, userName: currentUser.userName });
    }
  }, [currentUser]);

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setFormError(null);
    setSuccessMessage(null);

    const response = await updateUserInfo(userInfo);
    if (!response.success) {
      setFormError(
        "Falha ao atualizar o perfil. Verifique os dados e tente novamente.",
      );
      return;
    }
    setCurrentUser(response.data);
    setSuccessMessage("Perfil atualizado com sucesso!");
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="shadow-md rounded-lg p-6 space-y-4 flex flex-col gap-2"
    >
      <h2 className="text-lg font-bold mb-6 self-center">
        Editar Informações Comuns
      </h2>
      {formError && (
        <p
          className="text-red-500 text-sm bg-red-100 p-3 rounded"
          id="profile-form-error-message"
        >
          {formError}
        </p>
      )}
      {successMessage && (
        <p
          className="text-green-500 text-sm bg-green-100 p-3 rounded"
          id="profile-form-success-message"
        >
          {successMessage}
        </p>
      )}

      <div className="flex flex-col gap-1">
        <label htmlFor="name" className="block text-sm font-medium">
          Nome:
        </label>
        <Input
          type="text"
          name="name"
          id="name"
          value={userInfo.name}
          onChange={(e) =>
            setUserInfo((old) => ({ ...old, name: e.target.value }))
          }
          required
        />
      </div>

      <div className="flex flex-col gap-1">
        <label htmlFor="userName" className="block text-sm font-medium">
          Nome de Usuário:
        </label>
        <Input
          type="text"
          name="userName"
          id="userName"
          value={userInfo.userName}
          onChange={(e) =>
            setUserInfo((old) => ({ ...old, userName: e.target.value }))
          }
          required
          minLength={3}
        />
      </div>

      <Button
        className="self-center"
        scheme="primary"
        type="submit"
        id="save-profile-button"
      >
        Atualizar Informações
      </Button>
    </form>
  );
}
