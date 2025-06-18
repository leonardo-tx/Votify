import Button from "@/components/shared/Button";
import { currentUserAtom } from "@/libs/users/atoms/currentUserAtom";
import Head from "next/head";
import { useRouter } from "next/router";
import UserInfoForm from "./components/UserInfoForm";
import { useAtomValue } from "jotai";
import PasswordUserInfoForm from "./components/PasswordUserInfoForm";

export default function SettingsPage() {
  const router = useRouter();
  const currentUser = useAtomValue(currentUserAtom);

  if (currentUser === null) {
    return (
      <div className="flex flex-col items-center gap-4">
        <Head>
          <title>Erro - Votify</title>
        </Head>
        <p className="text-lg">
          Você precisa estar logado para editar o perfil.
        </p>
        <Button onClick={() => router.push("/login")} scheme="primary">
          Ir para Login
        </Button>
      </div>
    );
  }

  return (
    <>
      <Head>
        <title>Editar Perfil - Votify</title>
      </Head>
      <div className="container flex flex-col mx-auto p-4 max-w-lg">
        <h1 className="text-3xl font-bold mb-6 self-center">
          Editar Informações do Perfil
        </h1>
        <UserInfoForm />
        <hr />
        <PasswordUserInfoForm />
      </div>
    </>
  );
}
