import { useState } from "react";
import { useRouter } from "next/router";
import { createPoll } from "@/libs/api";
import Button from "@/components/shared/Button";
import Modal from "../shared/Modal";
import Input from "../shared/Input";
import { PollInsertDTO } from "@/libs/polls/PollInsertDTO";

interface CreatePollModalProps {
  isOpen: boolean;
  onClose: () => void;
}

type FormErrors = Partial<Record<keyof PollInsertDTO, string>> & { submit?: string };

const initialFormData: PollInsertDTO = {
  title: "",
  description: "",
  startDate: "",
  endDate: "",
  choiceLimitPerUser: 1,
  voteOptions: [""],
};

const validateTitle = (title: string): string | undefined => {
  if (!title || title.length < 5 || title.length > 50) {
    return "O título deve ter entre 5 e 50 caracteres";
  }
};

const validateDescription = (description: string): string | undefined => {
  if (description.length > 512) {
    return "A descrição deve ter no máximo 512 caracteres";
  }
};

const validateStartDate = (startDate: string): string | undefined => {
  if (startDate && new Date(startDate) <= new Date()) {
    return "A data de início deve ser maior que o momento atual";
  }
};

const validateEndDate = (endDate: string, startDate: string): string | undefined => {
  if (!endDate) {
    return "A data de término é obrigatória";
  }
  if (startDate && new Date(endDate) <= new Date(startDate)) {
    return "A data de término deve ser posterior à data de início";
  }
};

const validateVoteOptions = (voteOptions: string[]): string | undefined => {
  const validOptions = voteOptions.filter((opt) => opt.trim().length > 0);
  if (validOptions.length < 1 || validOptions.length > 5) {
    return "A enquete deve ter entre 1 e 5 opções de voto";
  }
  
  for (const option of validOptions) {
    if (option.length < 3 || option.length > 30) {
      return "Cada opção deve ter entre 3 e 30 caracteres";
    }
  }
};

const validateChoiceLimit = (choiceLimit: number, voteOptions: string[]): string | undefined => {
  const validOptions = voteOptions.filter((opt) => opt.trim().length > 0);
  if (choiceLimit < 1 || choiceLimit > validOptions.length) {
    return `O limite de escolhas deve estar entre 1 e ${validOptions.length}`;
  }
};

interface FormFieldProps {
  label: string;
  id: string;
  value: string | number;
  onChange: (value: string | number) => void;
  error?: string;
  type?: string;
  placeholder?: string;
  min?: number;
  max?: number;
  rows?: number;
}

const FormField = ({ 
  label, 
  id, 
  value, 
  onChange, 
  error, 
  type = "text", 
  placeholder, 
  min, 
  max, 
  rows 
}: FormFieldProps) => (
  <div>
    <label htmlFor={id} className="block text-sm font-medium mb-1">
      {label}
    </label>
    <Input
      id={id}
      type={type}
      value={value}
      onChange={(e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => 
        onChange(type === "number" ? parseInt(e.target.value) : e.target.value)
      }
      placeholder={placeholder}
      min={min}
      max={max}
      rows={rows}
      as={rows ? "textarea" : undefined}
    />
    {error && <p className="text-red-500 text-sm mt-1">{error}</p>}
  </div>
);

interface VoteOptionsProps {
  voteOptions: string[];
  onUpdateOption: (index: number, value: string) => void;
  onRemoveOption: (index: number) => void;
  onAddOption: () => void;
  error?: string;
}

const VoteOptions = ({ 
  voteOptions, 
  onUpdateOption, 
  onRemoveOption, 
  onAddOption, 
  error 
}: VoteOptionsProps) => (
  <div>
    <label className="block text-sm font-medium mb-1">
      Opções de Voto
    </label>
    {voteOptions.map((option, index) => (
      <div key={index} className="flex gap-2 mb-2">
        <Input
          id={`voteOption-${index}`}
          type="text"
          value={option}
          onChange={(e) => onUpdateOption(index, e.target.value)}
          placeholder={`Opção ${index + 1}`}
        />
        {voteOptions.length > 1 && (
          <Button
            id={`remove-option-${index}`}
            type="button"
            scheme="red"
            variant="text"
            onClick={() => onRemoveOption(index)}
            className="px-3 py-2 text-red-500 hover:text-red-700"
            aria-label={`Remover opção ${index + 1}`}
          >
            Remover
          </Button>
        )}
      </div>
    ))}
    {error && <p className="text-red-500 text-sm mt-1">{error}</p>}
    
    {voteOptions.length < 5 && (
      <Button
        id="add-option-button"
        scheme="primary"
        variant="text"
        type="button"
        onClick={onAddOption}
        className="mt-2 text-blue-500 hover:text-blue-700"
      >
        + Adicionar Opção
      </Button>
    )}
  </div>
);

export default function CreatePollModal({
  isOpen,
  onClose,
}: CreatePollModalProps) {
  const router = useRouter();
  const [formData, setFormData] = useState<PollInsertDTO>(initialFormData);
  const [errors, setErrors] = useState<FormErrors>({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  const validateForm = (): boolean => {
    const newErrors: FormErrors = {};
    
    newErrors.title = validateTitle(formData.title);
    newErrors.description = validateDescription(formData.description);
    newErrors.startDate = validateStartDate(formData.startDate);
    newErrors.endDate = validateEndDate(formData.endDate, formData.startDate);
    newErrors.voteOptions = validateVoteOptions(formData.voteOptions);
    newErrors.choiceLimitPerUser = validateChoiceLimit(formData.choiceLimitPerUser, formData.voteOptions);

    const filteredErrors = Object.fromEntries(
      Object.entries(newErrors).filter(([, value]) => value !== undefined)
    ) as FormErrors;

    setErrors(filteredErrors);
    return Object.keys(filteredErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validateForm()) return;

    setIsSubmitting(true);
    
    const response = await createPoll(formData);

    if (response.success && response.data) {
      onClose();
      router.push(`/polls/${response.data.id}`);
      window.location.href = "/home";
    } else {
      setErrors((prev) => ({
        ...prev,
        submit: response.errorMessage || "Erro ao criar enquete",
      }));
    }
    
    setIsSubmitting(false);
  };

  const updateField = (field: keyof PollInsertDTO, value: string | number) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  const addVoteOption = () => {
    if (formData.voteOptions.length < 5) {
      setFormData((prev) => ({
        ...prev,
        voteOptions: [...prev.voteOptions, ""],
      }));
    }
  };

  const removeVoteOption = (index: number) => {
    setFormData((prev) => ({
      ...prev,
      voteOptions: prev.voteOptions.filter((option, i) => i !== index),
    }));
  };

  const updateVoteOption = (index: number, value: string) => {
    setFormData((prev) => ({
      ...prev,
      voteOptions: prev.voteOptions.map((opt, i) =>
        i === index ? value : opt,
      ),
    }));
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose}>
      <h2 className="text-2xl font-bold mb-4">Criar Nova Enquete</h2>
      <form onSubmit={handleSubmit} className="space-y-4">
        <FormField
          label="Título"
          id="title"
          value={formData.title}
          onChange={(value) => updateField("title", value as string)}
          error={errors.title}
          placeholder="Digite o título da enquete"
        />

        <FormField
          label="Descrição"
          id="description"
          value={formData.description}
          onChange={(value) => updateField("description", value as string)}
          error={errors.description}
          placeholder="Digite a descrição da enquete"
          rows={3}
        />

        <div className="grid grid-cols-2 gap-4">
          <FormField
            label="Data de Início"
            id="startDate"
            value={formData.startDate}
            onChange={(value) => updateField("startDate", value as string)}
            error={errors.startDate}
            type="datetime-local"
          />

          <FormField
            label="Data de Término"
            id="endDate"
            value={formData.endDate}
            onChange={(value) => updateField("endDate", value as string)}
            error={errors.endDate}
            type="datetime-local"
          />
        </div>

        <FormField
          label="Limite de Escolhas por Usuário"
          id="choiceLimit"
          value={formData.choiceLimitPerUser}
          onChange={(value) => updateField("choiceLimitPerUser", value as number)}
          error={errors.choiceLimitPerUser}
          type="number"
          min={1}
          max={5}
        />

        <VoteOptions
          voteOptions={formData.voteOptions}
          onUpdateOption={updateVoteOption}
          onRemoveOption={removeVoteOption}
          onAddOption={addVoteOption}
          error={errors.voteOptions}
        />

        <div className="flex justify-end gap-2 mt-6">
          <Button
            id="cancel-button"
            scheme="red"
            type="button"
            onClick={onClose}
            variant="outline"
            disabled={isSubmitting}
          >
            Cancelar
          </Button>
          <Button 
            id="create-poll-button" 
            type="submit" 
            scheme="primary" 
            disabled={isSubmitting}
          >
            {isSubmitting ? "Criando..." : "Criar Enquete"}
          </Button>
        </div>
        
        {errors.submit && (
          <p id="submit-error" className="text-red-500 text-sm mt-2 text-center">
            {errors.submit}
          </p>
        )}
      </form>
    </Modal>
  );
}
