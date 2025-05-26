import Button from "@/components/shared/Button";
import Input from "@/components/shared/Input";
import UserSignUpDto from "@/libs/users/UserSignUpDTO";
import { FormEvent, useState } from "react";
import { signup } from "@/libs/api";
import {
  IoLockClosedOutline,
  IoMailOutline,
  IoPersonOutline,
  IoEyeOffOutline,
  IoEyeOutline,
} from "react-icons/io5";
import VotifyErrorCode from "@/libs/VotifyErrorCode";
import ConfirmationModal from "@/components/modals/ConfirmationModal";
import { useRouter } from "next/router";

export default function SignupPage() {
  const router = useRouter(); 
  const [user, setUser] = useState<UserSignUpDto>({
    name: "",
    userName: "",
    email: "",
    password: "",
    confirmPassword: "",
  });
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string>("");
  const [showPassword, setShowPassword] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const togglePasswordVisibility = () => {
    setShowPassword((prev) => !prev);
  };

  const validatePasswordConfirmation = (): boolean => {
    if (user.password !== user.confirmPassword) {
      setError("As senhas não coincidem.");
      return false;
    }
    return true;
  };

  const validateEmail = (): boolean => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(user.email)) {
      setError("Por favor, insira um email válido.");
      return false;
    }
    return true;
  };

  const handlerSignUp = async (e: FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError("");

    if (!validateEmail() || !validatePasswordConfirmation()) {
      setIsLoading(false);
      return;
    }

    try {
      const signupResponse = await signup(user);
      if (!signupResponse.success) {
        setError(getErrorMessage(signupResponse.errorCode));
        setIsLoading(false);
        return;
      }

      setIsModalOpen(true);
    } catch (err) {
      console.error("Erro ao cadastrar:", err);
      setError("Ocorreu um erro inesperado. Tente novamente.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="h-full flex items-center justify-center">
      <div className="w-full max-w-md p-8 rounded-2xl shadow-lg">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold">Votify</h1>
          <p className="mt-2">Crie sua conta</p>
        </div>

        <form className="space-y-6" onSubmit={handlerSignUp}>
          <div className="space-y-4">
            <Input
              id="register-name"
              type="text"
              required
              placeholder="Nome"
              className="w-full"
              variant="line"
              startElement={<IoPersonOutline size={20} />}
              value={user.name}
              onChange={(e) => setUser((old) => ({ ...old, name: e.target.value }))}
            />

            <Input
              id="register-userName"
              type="text"
              required
              placeholder="Nome de usuário"
              className="w-full"
              variant="line"
              startElement={<IoPersonOutline size={20} />}
              value={user.userName}
              onChange={(e) =>
                setUser((old) => ({ ...old, userName: e.target.value.trim() }))
              }
            />

            <Input
              id="register-email"
              type="email"
              required
              placeholder="Email"
              className="w-full"
              variant="line"
              startElement={<IoMailOutline size={20} />}
              value={user.email}
              onChange={(e) =>
                setUser((old) => ({ ...old, email: e.target.value.trim() }))
              }
            />

            <Input
              id="register-password"
              type={showPassword ? "text" : "password"}
              required
              placeholder="Senha"
              variant="line"
              startElement={<IoLockClosedOutline size={20} />}
              endElement={
                <button
                  type="button"
                  onClick={togglePasswordVisibility}
                  className="focus:outline-none"
                >
                  {showPassword ? <IoEyeOutline size={20} /> : <IoEyeOffOutline size={20} />}
                </button>
              }
              value={user.password}
              onChange={(e) => setUser((old) => ({ ...old, password: e.target.value }))}
            />

            <Input
              id="register-password-confirm"
              type={showPassword ? "text" : "password"}
              required
              placeholder="Confirme a senha"
              variant="line"
              startElement={<IoLockClosedOutline size={20} />}
              endElement={
                <button
                  type="button"
                  onClick={togglePasswordVisibility}
                  className="focus:outline-none"
                >
                  {showPassword ? <IoEyeOutline size={20} /> : <IoEyeOffOutline size={20} />}
                </button>
              }
              value={user.confirmPassword}
              onChange={(e) =>
                setUser((old) => ({ ...old, confirmPassword: e.target.value }))
              }
            />
          </div>

          {error && (
            <div id="register-alert" className="text-red-500 text-sm text-center">
              {error}
            </div>
          )}

          <Button
            type="submit"
            scheme="primary"
            id="register-submit-button"
            className="w-full bg-blue-600 hover:bg-blue-700 text-white py-2 rounded-md cursor-pointer"
            disabled={isLoading}
          >
            {isLoading ? "Criando conta..." : "Criar conta"}
          </Button>
        </form>

        <div className="mt-6 text-center">
          <p className="text-zinc-600">
            Já tem uma conta?{" "}
            <Button
              as="Link"
              variant="text"
              scheme="primary"
              id="login-link"
              href="/login"
              className="text-blue-600 hover:text-blue-700"
            >
              Entrar
            </Button>
          </p>
        </div>
      </div>

      {isModalOpen && (
        <ConfirmationModal
          title="Confirmação enviada"
          message={`Um email de confirmação foi enviado para ${user.email}. Verifique sua caixa de entrada.`}
          onClose={() => router.push("/login")} 
        />
      )}
    </div>
  );
}

function getErrorMessage(errorCode: VotifyErrorCode): string {
  switch (errorCode) {
    case VotifyErrorCode.EMAIL_ALREADY_EXISTS:
      return "Email já cadastrado.";
    case VotifyErrorCode.USER_NAME_ALREADY_EXISTS:
      return "Nome de usuário já cadastrado.";
    case VotifyErrorCode.PENDING_EMAIL_CONFIRMATION:
      return "Por favor, confirme seu email para continuar.";
    case VotifyErrorCode.LOGIN_UNAUTHORIZED:
      return "Email ou senha inválidos.";
    default:
      return "Não foi possível cadastrar o usuário.";
  }
}
