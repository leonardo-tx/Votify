import Button from "@/components/shared/Button";

export default function NotFound() {
  return (
    <div className="flex justify-center items-center h-full flex-col gap-5">
      <h1 className="text-3xl font-extrabold">Essa página não existe :c</h1>
      <Button as="Link" href="/home" scheme="primary">
        Voltar para Home
      </Button>
    </div>
  );
}
