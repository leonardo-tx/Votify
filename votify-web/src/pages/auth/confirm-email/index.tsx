import React, { useEffect, useState } from "react";
import { confirmEmail } from "@/libs/api";
import VotifyErrorCode from "@/libs/VotifyErrorCode";
import { useRouter } from "next/router";
import { GetServerSideProps } from "next";
import { useSetAtom } from "jotai";
import { currentUserAtom } from "@/libs/users/atoms/currentUserAtom";

interface Props {
  code: string;
  email: string;
}

function ConfirmEmailPage({ code, email }: Props) {
  const router = useRouter();
  const [message, setMessage] = useState<string>("Confirmando seu e-mail...");
  const setCurrentUser = useSetAtom(currentUserAtom);

  useEffect(() => {
    if (!email || !code) {
      setMessage(
        "Link inválido. Você será redirecionado para a página inicial.",
      );
      setTimeout(() => router.push("/"), 3000);
      return;
    }

    const handleConfirm = async () => {
      const response = await confirmEmail({ email, code });
      if (response.success) {
        setMessage("Email confirmado com sucesso! Redirecionando...");
        setCurrentUser(null);
        setTimeout(() => router.push("/login"), 3000);
      } else {
        setMessage(getErrorMessage(response.errorCode));
        setTimeout(() => router.push("/"), 4000);
      }
    };

    handleConfirm();
  }, [code, email, router, setCurrentUser]);

  return (
    <div style={{ textAlign: "center", marginTop: "50px" }}>
      <p>{message}</p>
    </div>
  );
}

function getErrorMessage(errorCode: VotifyErrorCode): string {
  switch (errorCode) {
    case VotifyErrorCode.EMAIL_ALREADY_CONFIRMED:
      return "O usuário não possui confirmações de e-mail pendentes.";
    case VotifyErrorCode.EMAIL_CONFIRMATION_CODE_INVALID:
      return "O link de confirmação de e-mail é invalido ou expirou.";
  }
  return "Não foi possível processar a solicitação.";
}

export const getServerSideProps: GetServerSideProps<Props> = async (
  context,
) => {
  const { code, email } = context.query;
  const searchCode = typeof code === "string" ? code : "";
  const searchEmail = typeof email === "string" ? email : "";

  return {
    props: {
      code: searchCode,
      email: searchEmail,
    },
  };
};

export default ConfirmEmailPage;
