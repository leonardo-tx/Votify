import Button from "@/components/shared/Button";
import Input from "@/components/shared/Input";
import { updateUserEmail } from "@/libs/api";
import { currentUserAtom } from "@/libs/users/atoms/currentUserAtom";
import UserUpdateEmailRequestDTO from "@/libs/users/UserUpdateEmailRequestDTO";
import { useAtomValue } from "jotai";
import { useState, useEffect } from "react";

export default function EmailUserInfoForm() {
  const currentUser = useAtomValue(currentUserAtom);
  const [userEmail, setUserEmail] = useState<UserUpdateEmailRequestDTO>({
    email: "",
  });
  const [formError, setFormError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  useEffect(() => {
    if (currentUser !== null) {
      setUserEmail({ email: currentUser.email });
    }
  }, [currentUser]);

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setFormError(null);
    setSuccessMessage(null);

    const response = await updateUserEmail(userEmail);
    if (!response.success) {
      setFormError(
        "Falha ao atualizar o perfil. Verifique os dados e tente novamente.",
      );
      return;
    }
    setSuccessMessage("Confirmação enviada ao e-mail antigo");
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="shadow-md rounded-lg p-6 space-y-4 flex flex-col gap-2"
    >
      <h2 className="text-lg font-bold mb-6 self-center">Editar E-mail</h2>
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
        <label htmlFor="email" className="block text-sm font-medium">
          E-mail:
        </label>
        <Input
          type="email"
          name="email"
          id="email"
          value={userEmail.email}
          onChange={(e) => setUserEmail({ email: e.target.value })}
          required
        />
      </div>
      <Button
        className="self-center"
        scheme="primary"
        type="submit"
        id="save-profile-button"
      >
        Alterar e-mail
      </Button>
    </form>
  );
}
