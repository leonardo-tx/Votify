import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { confirmEmail } from "@/libs/api";
import VotifyErrorCode from '@/libs/VotifyErrorCode';

function ConfirmEmailPage() {
    const location = useLocation();
    const navigate = useNavigate();
    const [status, setStatus] = useState<'loading' | 'success' | 'error' | 'invalid'>('loading');
    const [message, setMessage] = useState<string>('Confirmando seu e-mail...');

    function getQueryParam(param: string): string | null {
        return new URLSearchParams(location.search).get(param);
    }

    useEffect(() => {
        const email = getQueryParam('email');
        const code = getQueryParam('code');

        if (!email || !code) {
            setStatus('invalid');
            setMessage('Link inválido. Você será redirecionado.');
            setTimeout(() => navigate('/'), 3000);
            return;
        }

        const handleConfirm = async () => {
            try {
                const response = await confirmEmail({ email, code });
                if (response.success) {

                    setStatus('success');
                    setMessage('Email confirmado com sucesso! Redirecionando...');
                    setTimeout(() => navigate('/login'), 3000);
                } else {
                    setStatus('error');
                    setMessage(getErrorMessage(response.errorCode));
                    setTimeout(() => navigate('/'), 4000);
                }
            } catch (err: any) {
                setStatus('error');
                setMessage("Erro inesperado. Tente novamente." + err.response?.data?.errorCode);
            }
        };

        handleConfirm();
    }, [location, navigate]);

    return (
        <div style={{ textAlign: 'center', marginTop: '50px' }}>
            <p>{message}</p>
        </div>
    );
}

export function getErrorMessage(errorCode: VotifyErrorCode): string {
    switch (errorCode) {
        case VotifyErrorCode.EMAIL_ALREADY_CONFIRMED:
            return "This user already confirmed his email.";
        case VotifyErrorCode.EMAIL_CONFIRMATION_CODE_INVALID:
            return "Invalid email confirmation code sent.";
    }
    return "Não foi possível processar a solicitação.";
}

export default ConfirmEmailPage;