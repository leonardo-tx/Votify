import { render, screen, fireEvent } from "@testing-library/react";
import Home from "../index";
import { getAllActivePolls, getUserById } from "@/libs/api";
import PollSimpleView from "@/libs/polls/PollSimpleView";
import UserQueryView from "@/libs/users/UserQueryView";
import { useRouter } from "next/navigation";

// Mock the API functions
jest.mock("@/libs/api", () => ({
  getAllActivePolls: jest.fn(),
  getUserById: jest.fn(),
}));

// Mock next/navigation
jest.mock("next/navigation", () => ({
  useRouter: jest.fn(),
}));

describe("Home Page", () => {
  const mockRouter = {
    push: jest.fn(),
  };

  beforeEach(() => {
    (useRouter as jest.Mock).mockReturnValue(mockRouter);
    (getAllActivePolls as jest.Mock).mockReset();
    (getUserById as jest.Mock).mockReset();
    mockRouter.push.mockReset();
  });

  const mockPolls: { poll: PollSimpleView; user: UserQueryView | null }[] = [
    {
      poll: {
        id: 1,
        title: "Test Poll",
        description: "Test Description",
        startDate: "2024-01-01T00:00:00",
        endDate: "2024-01-02T00:00:00",
        responsibleId: 1,
      },
      user: {
        id: 1,
        name: "Test User",
        userName: "testuser",
        email: "test@example.com",
        role: "USER",
      },
    },
  ];

  it("should render the create poll button", () => {
    render(<Home polls={mockPolls} page={0} totalPages={1} />);
    expect(screen.getByText("Criar Nova Enquete")).toBeInTheDocument();
  });

  it("should open create poll modal when button is clicked", () => {
    render(<Home polls={mockPolls} page={0} totalPages={1} />);
    
    const createButton = screen.getByRole('button', { name: 'Criar Nova Enquete' });
    fireEvent.click(createButton);
    
    // Modal should be visible
    expect(screen.getByRole('heading', { name: 'Criar Nova Enquete' })).toBeInTheDocument();
    expect(screen.getByLabelText("Título")).toBeInTheDocument();
  });

  it("should render poll list", () => {
    render(<Home polls={mockPolls} page={0} totalPages={1} />);
    
    expect(screen.getByText("Test Poll")).toBeInTheDocument();
    expect(screen.getByText("Test Description")).toBeInTheDocument();
    expect(screen.getByText("Test User")).toBeInTheDocument();
  });

  it("should render pagination", () => {
    render(<Home polls={mockPolls} page={0} totalPages={3} />);
    
    // Check for pagination buttons
    expect(screen.getByRole('button', { name: '< Anterior' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Próximo >' })).toBeInTheDocument();
  });

  it("should handle empty polls list", () => {
    render(<Home polls={[]} page={0} totalPages={0} />);
    
    // The page should render without errors
    expect(screen.getByText("Criar Nova Enquete")).toBeInTheDocument();
  });
}); 