import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import CreatePollModal from "../CreatePollModal";
import { api } from "@/libs/api";
import { useRouter } from "next/navigation";

// Mock next/navigation
jest.mock("next/navigation", () => ({
  useRouter: jest.fn(),
}));

// Mock api
jest.mock("@/libs/api", () => ({
  api: {
    post: jest.fn(),
  },
}));

describe("CreatePollModal", () => {
  const mockRouter = {
    push: jest.fn(),
  };

  beforeEach(() => {
    (useRouter as jest.Mock).mockReturnValue(mockRouter);
    (api.post as jest.Mock).mockReset();
    mockRouter.push.mockReset();
  });

  it("should not render when isOpen is false", () => {
    render(<CreatePollModal isOpen={false} onClose={() => {}} />);
    expect(screen.queryByText("Criar Nova Enquete")).not.toBeInTheDocument();
  });

  it("should render all form fields when open", () => {
    render(<CreatePollModal isOpen={true} onClose={() => {}} />);
    
    expect(screen.getByText("Criar Nova Enquete")).toBeInTheDocument();
    expect(screen.getByLabelText("Título")).toBeInTheDocument();
    expect(screen.getByLabelText("Descrição")).toBeInTheDocument();
    expect(screen.getByLabelText("Data de Início")).toBeInTheDocument();
    expect(screen.getByLabelText("Data de Término")).toBeInTheDocument();
    expect(screen.getByLabelText("Limite de Escolhas por Usuário")).toBeInTheDocument();
    expect(screen.getByText("Opções de Voto")).toBeInTheDocument();
  });

  it("should show validation errors for empty required fields", async () => {
    render(<CreatePollModal isOpen={true} onClose={() => {}} />);
    
    const submitButton = screen.getByText("Criar Enquete");
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText("O título deve ter entre 5 e 50 caracteres")).toBeInTheDocument();
      expect(screen.getByText("A data de início é obrigatória")).toBeInTheDocument();
      expect(screen.getByText("A data de término é obrigatória")).toBeInTheDocument();
    });
  });

  it("should validate title length", async () => {
    render(<CreatePollModal isOpen={true} onClose={() => {}} />);
    
    const titleInput = screen.getByLabelText("Título");
    fireEvent.change(titleInput, { target: { value: "123" } });
    
    const submitButton = screen.getByText("Criar Enquete");
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText("O título deve ter entre 5 e 50 caracteres")).toBeInTheDocument();
    });
  });

  it("should validate end date is after start date", async () => {
    render(<CreatePollModal isOpen={true} onClose={() => {}} />);
    
    const startDateInput = screen.getByLabelText("Data de Início");
    const endDateInput = screen.getByLabelText("Data de Término");
    
    fireEvent.change(startDateInput, { target: { value: "2024-12-31T23:59" } });
    fireEvent.change(endDateInput, { target: { value: "2024-12-31T23:58" } });
    
    const submitButton = screen.getByText("Criar Enquete");
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText("A data de término deve ser posterior à data de início")).toBeInTheDocument();
    });
  });

  it("should add and remove vote options", () => {
    render(<CreatePollModal isOpen={true} onClose={() => {}} />);
    
    // Add a new option
    const addButton = screen.getByText("+ Adicionar Opção");
    fireEvent.click(addButton);
    
    // Should now have two option inputs
    const optionInputs = screen.getAllByPlaceholderText(/Opção \d+/);
    expect(optionInputs).toHaveLength(2);
    
    // Remove the second option
    const removeButton = screen.getByLabelText("Remover opção 2");
    fireEvent.click(removeButton);
    
    // Should be back to one option
    const remainingOptions = screen.getAllByPlaceholderText(/Opção \d+/);
    expect(remainingOptions).toHaveLength(1);
  });

  it("should successfully create a poll and redirect", async () => {
    const mockResponse = {
      data: {
        success: true,
        data: {
          id: 123,
        },
      },
    };
    
    (api.post as jest.Mock).mockResolvedValue(mockResponse);
    
    render(<CreatePollModal isOpen={true} onClose={() => {}} />);
    
    // Fill in the form
    fireEvent.change(screen.getByLabelText("Título"), { target: { value: "Test Poll" } });
    fireEvent.change(screen.getByLabelText("Descrição"), { target: { value: "Test Description" } });
    fireEvent.change(screen.getByLabelText("Data de Início"), { target: { value: "2024-01-01T00:00" } });
    fireEvent.change(screen.getByLabelText("Data de Término"), { target: { value: "2024-01-02T00:00" } });
    fireEvent.change(screen.getByLabelText("Limite de Escolhas por Usuário"), { target: { value: "1" } });
    fireEvent.change(screen.getByPlaceholderText("Opção 1"), { target: { value: "Option 1" } });
    
    // Submit the form
    const submitButton = screen.getByText("Criar Enquete");
    fireEvent.click(submitButton);
    
    await waitFor(() => {
      expect(api.post).toHaveBeenCalledWith("/polls", expect.any(Object));
      expect(mockRouter.push).toHaveBeenCalledWith("/polls/123");
    });
  });

  it("should handle API errors gracefully", async () => {
    (api.post as jest.Mock).mockRejectedValue(new Error("API Error"));
    
    const consoleSpy = jest.spyOn(console, "error").mockImplementation(() => {});
    
    render(<CreatePollModal isOpen={true} onClose={() => {}} />);
    
    // Fill in the form
    fireEvent.change(screen.getByLabelText("Título"), { target: { value: "Test Poll" } });
    fireEvent.change(screen.getByLabelText("Descrição"), { target: { value: "Test Description" } });
    fireEvent.change(screen.getByLabelText("Data de Início"), { target: { value: "2024-01-01T00:00" } });
    fireEvent.change(screen.getByLabelText("Data de Término"), { target: { value: "2024-01-02T00:00" } });
    fireEvent.change(screen.getByLabelText("Limite de Escolhas por Usuário"), { target: { value: "1" } });
    fireEvent.change(screen.getByPlaceholderText("Opção 1"), { target: { value: "Option 1" } });
    
    // Submit the form
    const submitButton = screen.getByText("Criar Enquete");
    fireEvent.click(submitButton);
    
    await waitFor(() => {
      expect(consoleSpy).toHaveBeenCalledWith("Erro ao criar enquete:", expect.any(Error));
    });
    
    consoleSpy.mockRestore();
  });
}); 