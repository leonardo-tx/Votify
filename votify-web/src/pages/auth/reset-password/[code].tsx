import { useState, FormEvent } from "react";
import { useRouter } from "next/router";
import { resetPassword } from "@/libs/api";
import Input from "@/components/shared/Input";
import Button from "@/components/shared/Button";
import Head from "next/head";
import UserPasswordResetConfirmDTO from "@/libs/users/UserPasswordResetConfirmDTO";
import VotifyErrorCode from "@/libs/VotifyErrorCode";

export default function ResetPasswordPage() {
    const router = useRouter();
    const { code } = router.query;
    const [formData, setFormData] = useState({
        newPassword: "",
        confirmPassword: ""
    });
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState(false);

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        setError(null);
        setSuccess(null);

        if (!code || typeof code !== "string") {
            setError("Código de recuperação inválido.");
            return;
        }

        if (formData.newPassword !== formData.confirmPassword) {
            setError("As senhas não coincidem.");
            return;
        }

        setIsLoading(true);

        try {
            const passwordResetConfirm: UserPasswordResetConfirmDTO = {
                code,
                newPassword: formData.newPassword
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
        } catch (err) {
            console.error(err);
            setError("Erro inesperado. Tente novamente.");
        } finally {
            setIsLoading(false);
        }
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { id, value } = e.target;
        setFormData((prev) => ({ ...prev, [id]: value }));
    };

    return (
        <>
            <Head>
                <title>Redefinir Senha - Votify</title>
            </Head>

            <div className="h-full flex items-center justify-center">
                <div className="w-full max-w-md p-8 rounded-2xl shadow-lg">
                    <div className="text-center mb-8">
                        <h1 className="text-3xl font-bold">Votify</h1>
                        <p className="mt-2">Digite sua nova senha</p>
                    </div>

                    <form onSubmit={handleSubmit} className="space-y-6">
                        <Input
                            id="new-password"
                            type="password"
                            required
                            placeholder="Nova senha"
                            className="w-full"
                            variant="line"
                            value={formData.newPassword}
                            onChange={handleChange}
                        />

                        <Input
                            id="confirm-password"
                            type="password"
                            required
                            placeholder="Confirmar nova senha"
                            className="w-full"
                            variant="line"
                            value={formData.confirmPassword}
                            onChange={handleChange}
                        />

                        {error && <div className="text-red-500 text-sm text-center">{error}</div>}
                        {success && <div className="text-green-500 text-sm text-center">{success}</div>}

                        <Button
                            type="submit"
                            scheme="primary"
                            className="w-full bg-blue-600 hover:bg-blue-700 text-white py-2 rounded-md cursor-pointer"
                            disabled={isLoading}
                        >
                            {isLoading ? "Redefinindo..." : "Redefinir Senha"}
                        </Button>
                    </form>
                </div>
            </div>
        </>
    );
}

function getErrorMessage(errorCode?: VotifyErrorCode): string {
    switch (errorCode) {
        case VotifyErrorCode.PASSWORD_RESET_CODE_INVALID:
            return "Código de recuperação inválido ou expirado.";
        case VotifyErrorCode.PASSWORD_INVALID_LENGTH:
            return "O número de caracteres em uma senha deve ser maior ou igual a 8.";
        case VotifyErrorCode.PASSWORD_INVALID_CHARACTER:
            return "A senha não deve conter caracteres inválidos.";
        default:
            return "Não foi possível redefinir sua senha.";
    }
}
