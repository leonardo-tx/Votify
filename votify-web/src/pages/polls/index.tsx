import { GetServerSideProps } from 'next';
import Link from 'next/link';
import axios from 'axios';

interface VoteOption {
    name: string;
    voteCount: number;
}

interface Poll {
    id: number;
    title: string;
    description: string;
    startDate: string;
    endDate: string;
    choiceLimitPerUser: number;
    responsibleId: number;
    voteOptions: VoteOption[];
    myChoices: number;
}

interface ApiResponse {
    success: boolean;
    data: Poll[];
    errorCode: number | null;
    errorMessage: string | null;
}

interface PollsPageProps {
    polls: Poll[];
}

export default function PollsIndexPage({ polls }: PollsPageProps) {
    return (
        <div style={{ margin: '2rem' }}>
            <h1>Lista de Enquetes</h1>
            <ul>
                {polls.map(poll => (
                    <li key={poll.id}>
                        <Link href={`/polls/${poll.id}`}>
                            <a>{poll.title}</a>
                        </Link>
                    </li>
                ))}
            </ul>
        </div>
    );
}

export const getServerSideProps: GetServerSideProps<PollsPageProps> = async () => {
    try {
        const response = await axios.get<ApiResponse>('http://localhost:8081/polls');
        const polls = response.data.success ? response.data.data : [];
        return { props: { polls } };
    } catch (error) {
        console.error('Erro ao buscar a lista de enquetes:', error);
        return { props: { polls: [] } };
    }
};
