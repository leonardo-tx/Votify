import { FormEvent, useState } from "react";
import { forgotPassword } from "@/libs/api";
import Input from "@/components/shared/Input";
import Button from "@/components/shared/Button";
import Head from "next/head";
import UserPasswordResetRequestDto from "@/libs/users/UserPasswordResetRequestDto";
import VotifyErrorCode from "@/libs/VotifyErrorCode";

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);
    setIsLoading(true);

    const passwordResetRequest: UserPasswordResetRequestDto = { email };
    const response = await forgotPassword(passwordResetRequest);

    if (response.success) {
      setSuccess("O link de reset de senha foi enviado para o seu email.");
    } else {
      setError(getErrorMessage(response.errorCode));
    }
    setIsLoading(false);
  };

  return (
    <>
      <Head>
        <title>Esqueci minha senha - Votify</title>
      </Head>

      <div className="h-full flex items-center justify-center">
        <div className="w-full max-w-md p-8 rounded-2xl shadow-lg">
          <div className="text-center mb-8">
            <h1 className="text-3xl font-bold">Votify</h1>
            <p className="mt-2">Recupere sua senha</p>
          </div>

          <form onSubmit={handleSubmit} className="space-y-6">
            <Input
              id="email"
              type="email"
              required
              placeholder="Seu email"
              className="w-full"
              variant="line"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />

            {error && (
              <div className="text-red-500 text-sm text-center">{error}</div>
            )}
            {success && (
              <div className="text-green-500 text-sm text-center">
                {success}
              </div>
            )}

            <Button
              type="submit"
              scheme="primary"
              className="w-full bg-blue-600 hover:bg-blue-700 text-white py-2 rounded-md cursor-pointer"
              disabled={isLoading}
            >
              {isLoading ? "Enviando..." : "Enviar pedido de reset de senha"}
            </Button>
          </form>
        </div>
      </div>
    </>
  );
}

function getErrorMessage(errorCode?: VotifyErrorCode): string {
  switch (errorCode) {
    case VotifyErrorCode.PASSWORD_RESET_REQUEST_EXISTS:
      return "Um reset de senha já foi requisitado para esse usuário.";
    case VotifyErrorCode.USER_NOT_FOUND:
      return "Não existe um usuário com o e-mail inserido.";
    default:
      return "Não foi possível enviar um pedido para resetar sua senha, tente novamente.";
  }
}
