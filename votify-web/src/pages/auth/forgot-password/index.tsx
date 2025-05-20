import {FormEvent, useState} from "react";
import {useRouter} from "next/router";
import {forgotPassword, resetPassword} from "@/libs/api";
import Input from "@/components/shared/Input";
import Button from "@/components/shared/Button";
import Head from "next/head";
import UserPasswordResetRequestDto from "@/libs/users/UserPasswordResetRequestDto";
import UserPasswordResetConfirmDTO from "@/libs/users/UserPasswordResetConfirmDTO";
import VotifyErrorCode from "@/libs/VotifyErrorCode";

export default function ForgotPasswordPage() {
    const [step, setStep] = useState<1 | 2 | 3>(1);
    const [email, setEmail] = useState("");
    const [code, setCode] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState(false);
    const router = useRouter();

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        setError(null);
        setIsLoading(true);

        try {
            if (step === 1) {
                const passwordResetRequest: UserPasswordResetRequestDto = {email: email};
                const response = await forgotPassword(passwordResetRequest);
                
                if (response.success) {
                    console.log("O código de verificação é: "+response.data?.code);
                    setStep(2);
                } else {
                    setError(getErrorMessage(response.errorCode));
                }
            } else if (step === 2) {
                if (newPassword !== confirmPassword) {
                    setError("As senhas não coincidem.");
                    setIsLoading(false);
                    return;
                }

                const passwordResetConfirm: UserPasswordResetConfirmDTO = {
                    code,
                    newPassword
                };

                const response = await resetPassword(passwordResetConfirm);

                if (response.success) {
                    setSuccess("Senha redefinida com sucesso! Redirecionando para o login...");
                    setTimeout(() => {
                        router.push("/login");
                    }, 3000);
                } else {
                    setError(getErrorMessage(response.errorCode));
                }
            }
        } catch (err: any) {
            setError("Erro inesperado. Tente novamente. " + err.response?.data?.errorCode);
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
                        <p className="mt-2">
                            {step === 1 && "Recupere sua senha"}
                            {step === 2 && "Digite o código de verificação"}
                            {step === 3 && "Defina uma nova senha"}
                        </p>
                    </div>

                    <form onSubmit={handleSubmit} className="space-y-6">
                        {step === 1 && (
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
                        )}

                        {step === 2 && (
                            <>
                                <Input
                                    id="code"
                                    type="text"
                                    required
                                    placeholder="Código de verificação"
                                    className="w-full"
                                    variant="line"
                                    value={code}
                                    onChange={(e) => setCode(e.target.value)}
                                />
                                <Input
                                    id="new-password"
                                    type="password"
                                    required
                                    placeholder="Nova senha"
                                    className="w-full"
                                    variant="line"
                                    value={newPassword}
                                    onChange={(e) => setNewPassword(e.target.value)}
                                />
                                <Input
                                    id="confirm-password"
                                    type="password"
                                    required
                                    placeholder="Confirmar nova senha"
                                    className="w-full"
                                    variant="line"
                                    value={confirmPassword}
                                    onChange={(e) => setConfirmPassword(e.target.value)}
                                />
                            </>
                        )}

                        {error && (
                            <div className="text-red-500 text-sm text-center">
                                {error}
                            </div>
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
                            {isLoading
                                ? "Processando..."
                                : step === 1
                                    ? "Enviar Código"
                                    : "Redefinir Senha"}
                        </Button>
                    </form>
                </div>
            </div>
        </>
    );
}

function getErrorMessage(errorCode: VotifyErrorCode): string {
    switch (errorCode) {
        case VotifyErrorCode.PASSWORD_RESET_EMAIL_NOT_FOUND:
            return "Email não existe ou está incorreto.";
        case VotifyErrorCode.PASSWORD_RESET_CODE_INVALID:
            return "Código de recuperação inválido ou expirado.";
        case VotifyErrorCode.PASSWORD_INVALID_LENGTH:
            return "O número de caracteres em uma senha deve ser maior ou igual a 8."
        case VotifyErrorCode.PASSWORD_INVALID_CHARACTER:
            return "A senha não deve conter caracteres inválidos."
    }
    return "Não foi possível recuperar sua senha.";
}
