import Button from "@/components/shared/Button";

interface Props {
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
}

export default function Pagination({
  currentPage,
  totalPages,
  onPageChange,
}: Props) {
  if (totalPages <= 1) return null;

  return (
    <div className="flex justify-center mt-4 space-x-2">
      <Button
        onClick={() => onPageChange(Math.max(0, currentPage - 1))}
        disabled={currentPage === 0}
        className={
          currentPage === 0 ? "opacity-50 cursor-not-allowed" : "cursor-pointer"
        }
        id="previous-page"
        variant="text"
      >
        {"< Anterior"}
      </Button>
      <Button
        onClick={() => onPageChange(Math.min(totalPages - 1, currentPage + 1))}
        disabled={currentPage >= totalPages - 1}
        className={
          currentPage >= totalPages - 1
            ? "opacity-50 cursor-not-allowed"
            : "cursor-pointer"
        }
        id="next-page"
        variant="text"
      >
        {"Próximo >"}
      </Button>
    </div>
  );
}
