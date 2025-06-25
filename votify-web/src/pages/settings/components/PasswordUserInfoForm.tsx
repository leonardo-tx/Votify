import Button from "@/components/shared/Button";
import Input from "@/components/shared/Input";
import { updateUserPassword } from "@/libs/api";
import UserUpdatePasswordRequestDTO from "@/libs/users/UserUpdatePasswordRequestDTO";
import { useState } from "react";

export default function PasswordUserInfoForm() {
  const [userInfo, setUserInfo] = useState<UserUpdatePasswordRequestDTO>({
    oldPassword: "",
    newPassword: "",
  });
  const [formError, setFormError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setFormError(null);
    setSuccessMessage(null);

    const response = await updateUserPassword(userInfo);
    if (!response.success) {
      setFormError(
        "Falha ao atualizar a senha. Verifique os dados e tente novamente.",
      );
      return;
    }
    setSuccessMessage("Senha atualizada com sucesso!");
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
          Senha antiga:
        </label>
        <Input
          type="password"
          name="oldPassword"
          id="oldPassword"
          value={userInfo.oldPassword}
          onChange={(e) =>
            setUserInfo((old) => ({ ...old, oldPassword: e.target.value }))
          }
          required
        />
      </div>

      <div className="flex flex-col gap-1">
        <label htmlFor="userName" className="block text-sm font-medium">
          Senha nova:
        </label>
        <Input
          type="password"
          name="newPassword"
          id="newPassword"
          value={userInfo.newPassword}
          onChange={(e) =>
            setUserInfo((old) => ({ ...old, newPassword: e.target.value }))
          }
          required
          minLength={3}
        />
      </div>

      <Button
        className="self-center"
        scheme="primary"
        type="submit"
        id="save-password-button"
      >
        Alterar Senha
      </Button>
    </form>
  );
}
