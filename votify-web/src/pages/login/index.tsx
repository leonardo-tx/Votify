import { useState, FormEvent } from 'react';
import { useRouter } from 'next/router';
import Button from '@/components/shared/Button';
import Input from '@/components/shared/Input';
import { IoMailOutline, IoLockClosedOutline } from 'react-icons/io5';

interface LoginRequest {
  email: string;
  password: string;
}

export default function LoginPage() {
  const router = useRouter();
  const [credentials, setCredentials] = useState<LoginRequest>({
    email: '',
    password: '',
  });
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string>('');

  // Use an environment variable for the API URL with a fallback for local development
  const apiUrl = 'http://localhost:8081';

  const handleLogin = async (e: FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');

    try {
      const response = await fetch(`${apiUrl}/auth/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(credentials),
      });

      const data = await response.json();

      if (!response.ok) {
        // Use the error message returned by the backend if available
        const errorMessage = data?.message || 'Credenciais inválidas';
        throw new Error(errorMessage);
      }

      // Save the access token and redirect to dashboard
      localStorage.setItem('accessToken', data.accessToken);
      router.push('/home');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Falha ao realizar login');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center">
      <div className="w-full max-w-md p-8 rounded-2xl shadow-lg">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold">Votify</h1>
          <p className="mt-2">Entre na sua conta</p>
        </div>

        <form onSubmit={handleLogin} className="space-y-6">
          <div className="space-y-4">
            <Input
              id="email"
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
              id="password"
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
            <div className="text-red-500 text-sm text-center">
              {error}
            </div>
          )}

          <Button
            as="Link"
            variant="text"
            scheme="primary"
            href="/auth/forgot-password"
            className="text-sm text-blue-600 hover:text-blue-700"
          >
            Esqueceu a senha?
          </Button>

          <Button
            type="submit"
            scheme="primary"
            className="w-full bg-blue-600 hover:bg-blue-700 text-white py-2 rounded-md"
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