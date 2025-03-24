import Link from "next/link";

export default function Home() {
  return (
    <main className="flex flex-col items-center justify-center min-h-screen bg-gray-50 dark:bg-gray-900 text-gray-900 dark:text-gray-100 px-4">
      <h1 className="text-5xl font-bold mb-6">Bem‑vindo ao Votify</h1>
      <p className="text-lg mb-10">Sua plataforma de enquetes simples e eficiente.</p>
      <div className="flex gap-4">
        <Link href="/auth/login" className="px-6 py-2 bg-blue-500 text-white rounded hover:bg-blue-600">
          Entrar
        </Link>
        <Link href="/auth/register" className="px-6 py-2 border border-blue-500 text-blue-500 rounded hover:bg-blue-100">
          Criar Conta
        </Link>
      </div>
    </main>
  );
}
