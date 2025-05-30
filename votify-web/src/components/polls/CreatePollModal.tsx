import { useState } from "react";
import { useRouter } from "next/navigation";
import { api } from "@/libs/api";
import Button from "@/components/shared/Button";

interface CreatePollModalProps {
  isOpen: boolean;
  onClose: () => void;
}

interface FormData {
  title: string;
  description: string;
  startDate: string;
  endDate: string;
  choiceLimitPerUser: number;
  voteOptions: string[];
}

export default function CreatePollModal({ isOpen, onClose }: CreatePollModalProps) {
  const router = useRouter();
  const [formData, setFormData] = useState<FormData>({
    title: "",
    description: "",
    startDate: "",
    endDate: "",
    choiceLimitPerUser: 1,
    voteOptions: [""],
  });
  const [errors, setErrors] = useState<Partial<Record<keyof FormData, string>> & { submit?: string }>({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  if (!isOpen) return null;

  const validateForm = (): boolean => {
    const newErrors: Partial<Record<keyof FormData, string>> = {};
    const now = new Date();
    const oneMinuteFromNow = new Date(now.getTime() + 60000); // Add 1 minute
    
    if (!formData.title || formData.title.length < 5 || formData.title.length > 50) {
      newErrors.title = "O título deve ter entre 5 e 50 caracteres";
    }
    
    if (formData.description.length > 512) {
      newErrors.description = "A descrição deve ter no máximo 512 caracteres";
    }
    
    if (!formData.startDate) {
      newErrors.startDate = "A data de início é obrigatória";
    } else if (new Date(formData.startDate) < oneMinuteFromNow) {
      newErrors.startDate = "A data de início deve ser pelo menos 1 minuto no futuro";
    }
    
    if (!formData.endDate) {
      newErrors.endDate = "A data de término é obrigatória";
    } else if (formData.startDate && new Date(formData.endDate) <= new Date(formData.startDate)) {
      newErrors.endDate = "A data de término deve ser posterior à data de início";
    }
    
    const validOptions = formData.voteOptions.filter(opt => opt.trim().length > 0);
    if (validOptions.length < 1 || validOptions.length > 5) {
      newErrors.voteOptions = "A enquete deve ter entre 1 e 5 opções de voto";
    } else {
      for (const option of validOptions) {
        if (option.length < 3 || option.length > 30) {
          newErrors.voteOptions = "Cada opção deve ter entre 3 e 30 caracteres";
          break;
        }
      }
    }
    
    if (formData.choiceLimitPerUser < 1 || formData.choiceLimitPerUser > validOptions.length) {
      newErrors.choiceLimitPerUser = `O limite de escolhas deve estar entre 1 e ${validOptions.length}`;
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateForm()) return;
    
    setIsSubmitting(true);
    try {
      const response = await api.post("/polls", {
        title: formData.title,
        description: formData.description,
        startDate: new Date(formData.startDate).toISOString(),
        endDate: new Date(formData.endDate).toISOString(),
        choiceLimitPerUser: formData.choiceLimitPerUser,
        userRegistration: false,
        voteOptions: formData.voteOptions
          .filter(opt => opt.trim().length > 0)
          .map(name => ({ name })),
      });

      if (response.data.success) {
        onClose();
        router.push(`/polls/${response.data.data.id}`);
        router.refresh();
        // Force a hard refresh of the home page
        window.location.href = '/home';
      }
    } catch (error: any) {
      console.error("Erro ao criar enquete:", error);
      if (error.response) {
        console.error("Detalhes do erro:", error.response.data);
        setErrors(prev => ({
          ...prev,
          submit: error.response.data.errorMessage || "Erro ao criar enquete"
        }));
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  const addVoteOption = () => {
    if (formData.voteOptions.length < 5) {
      setFormData(prev => ({
        ...prev,
        voteOptions: [...prev.voteOptions, ""]
      }));
    }
  };

  const removeVoteOption = (index: number) => {
    setFormData(prev => ({
      ...prev,
      voteOptions: prev.voteOptions.filter((_, i) => i !== index)
    }));
  };

  const updateVoteOption = (index: number, value: string) => {
    setFormData(prev => ({
      ...prev,
      voteOptions: prev.voteOptions.map((opt, i) => i === index ? value : opt)
    }));
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white dark:bg-gray-800 rounded-lg p-6 w-full max-w-2xl max-h-[90vh] overflow-y-auto">
        <h2 className="text-2xl font-bold mb-4">Criar Nova Enquete</h2>
        
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label htmlFor="title" className="block text-sm font-medium mb-1">Título</label>
            <input
              id="title"
              type="text"
              value={formData.title}
              onChange={(e) => setFormData(prev => ({ ...prev, title: e.target.value }))}
              className="w-full p-2 border rounded dark:bg-gray-700 dark:border-gray-600"
              placeholder="Digite o título da enquete"
            />
            {errors.title && <p className="text-red-500 text-sm mt-1">{errors.title}</p>}
          </div>

          <div>
            <label htmlFor="description" className="block text-sm font-medium mb-1">Descrição</label>
            <textarea
              id="description"
              value={formData.description}
              onChange={(e) => setFormData(prev => ({ ...prev, description: e.target.value }))}
              className="w-full p-2 border rounded dark:bg-gray-700 dark:border-gray-600"
              placeholder="Digite a descrição da enquete"
              rows={3}
            />
            {errors.description && <p className="text-red-500 text-sm mt-1">{errors.description}</p>}
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label htmlFor="startDate" className="block text-sm font-medium mb-1">Data de Início</label>
              <input
                id="startDate"
                type="datetime-local"
                value={formData.startDate}
                onChange={(e) => setFormData(prev => ({ ...prev, startDate: e.target.value }))}
                className="w-full p-2 border rounded dark:bg-gray-700 dark:border-gray-600"
              />
              {errors.startDate && <p className="text-red-500 text-sm mt-1">{errors.startDate}</p>}
            </div>

            <div>
              <label htmlFor="endDate" className="block text-sm font-medium mb-1">Data de Término</label>
              <input
                id="endDate"
                type="datetime-local"
                value={formData.endDate}
                onChange={(e) => setFormData(prev => ({ ...prev, endDate: e.target.value }))}
                className="w-full p-2 border rounded dark:bg-gray-700 dark:border-gray-600"
              />
              {errors.endDate && <p className="text-red-500 text-sm mt-1">{errors.endDate}</p>}
            </div>
          </div>

          <div>
            <label htmlFor="choiceLimit" className="block text-sm font-medium mb-1">Limite de Escolhas por Usuário</label>
            <input
              id="choiceLimit"
              type="number"
              min="1"
              max="5"
              value={formData.choiceLimitPerUser}
              onChange={(e) => setFormData(prev => ({ ...prev, choiceLimitPerUser: parseInt(e.target.value) }))}
              className="w-full p-2 border rounded dark:bg-gray-700 dark:border-gray-600"
            />
            {errors.choiceLimitPerUser && <p className="text-red-500 text-sm mt-1">{errors.choiceLimitPerUser}</p>}
          </div>

          <div>
            <label className="block text-sm font-medium mb-1">Opções de Voto</label>
            {formData.voteOptions.map((option, index) => (
              <div key={index} className="flex gap-2 mb-2">
                <input
                  id={`voteOption-${index}`}
                  type="text"
                  value={option}
                  onChange={(e) => updateVoteOption(index, e.target.value)}
                  className="flex-1 p-2 border rounded dark:bg-gray-700 dark:border-gray-600"
                  placeholder={`Opção ${index + 1}`}
                />
                {formData.voteOptions.length > 1 && (
                  <button
                    type="button"
                    onClick={() => removeVoteOption(index)}
                    className="px-3 py-2 text-red-500 hover:text-red-700"
                    aria-label={`Remover opção ${index + 1}`}
                  >
                    Remover
                  </button>
                )}
              </div>
            ))}
            {errors.voteOptions && <p className="text-red-500 text-sm mt-1">{errors.voteOptions}</p>}
            
            {formData.voteOptions.length < 5 && (
              <button
                type="button"
                onClick={addVoteOption}
                className="mt-2 text-blue-500 hover:text-blue-700"
              >
                + Adicionar Opção
              </button>
            )}
          </div>

          <div className="flex justify-end gap-2 mt-6">
            <Button
              type="button"
              onClick={onClose}
              variant="outline"
              disabled={isSubmitting}
            >
              Cancelar
            </Button>
            <Button
              type="submit"
              scheme="primary"
              disabled={isSubmitting}
            >
              {isSubmitting ? "Criando..." : "Criar Enquete"}
            </Button>
          </div>
          {errors.submit && (
            <p className="text-red-500 text-sm mt-2 text-center">{errors.submit}</p>
          )}
        </form>
      </div>
    </div>
  );
} 