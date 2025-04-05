import { useState, FormEvent } from 'react';
import { useRouter } from 'next/router';
import Button from '@/components/shared/Button';
import Input from '@/components/shared/Input';
import { IoMailOutline, IoLockClosedOutline } from 'react-icons/io5';
import { login } from '@/libs/api';
import UserLoginDTO from '@/libs/users/UserLoginDTO';

export default function LoginPage() {
  const router = useRouter();
  const [credentials, setCredentials] = useState<UserLoginDTO>({
    email: '',
    password: ''
  });
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string>('');

  const handleLogin = async (e: FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');

    const response = await login(credentials);
    try {
      if (response.success) {
        router.push('/home');
      } else {
        setError(response.data?.errorMessage);
      } 
    } catch (error: any) {
      setError(error.message);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="inset-0 flex items-center justify-center">
      <div className="w-full max-w-md p-8 rounded-2xl shadow-lg">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold">Votify</h1>
          <p className="mt-2">Entre na sua conta</p>
        </div>

        <form onSubmit={handleLogin} className="space-y-6">
          <div className="space-y-4">
            <Input
              id="login-email"
              type="text"
              required
              placeholder="Email"
              className="w-full"
              variant="line"
              startElement={<IoMailOutline size={20} />}
              value={credentials.email}
              onChange={(e) =>
                setCredentials({
                  ...credentials,
                  email: e.target.value.trim(),
                })
              }
            />

            <Input
              id="login-password"
              type="password"
              required
              placeholder="Senha"
              variant="line"
              startElement={<IoLockClosedOutline size={20} />}
              value={credentials.password}
              onChange={(e) =>
                setCredentials({
                  ...credentials,
                  password: e.target.value,
                })
              }
            />
          </div>

          {error && (
            <div className="text-red-500 text-sm text-center">{error}</div>
          )}

          <Button
            as="Link"
            variant="text"
            scheme="primary"
            id="forgot-password-link"
            href="/auth/forgot-password"
            className="text-sm text-blue-600 hover:text-blue-700"
          >
            Esqueceu a senha?
          </Button>

          <Button
            type="submit"
            scheme="primary"
            id="login-submit-button"
            className="w-full bg-blue-600 hover:bg-blue-700 text-white py-2 rounded-md cursor-pointer"
            disabled={isLoading}
          >
            {isLoading ? 'Entrando...' : 'Entrar'}
          </Button>
        </form>

        <div className="mt-6 text-center">
          <p className="text-zinc-600">
            Não tem uma conta?{' '}
            <Button
              as="Link"
              variant="text"
              scheme="primary"
              id="create-account-link"
              href="/auth/register"
              className="text-blue-600 hover:text-blue-700"
            >
              Criar conta
            </Button>
          </p>
        </div>
      </div>
    </div>
  );
}