import Link from 'next/link';

interface Poll {
  id: number;
  title: string;
  description?: string | null;
}

interface UserPollsProps {
  polls: Poll[];
}

const UserPolls: React.FC<UserPollsProps> = ({ polls }) => {
  if (!polls || polls.length === 0) {
    return <p className="text-gray-500">Este usuário ainda não criou nenhuma enquete.</p>;
  }

  return (
    <ul className="space-y-4">
      {polls.map((poll) => (
        <li key={poll.id} className="bg-white p-4 shadow rounded-md hover:shadow-lg transition-shadow">
          <Link href={`/polls/${poll.id}`} className="text-blue-600 hover:text-blue-800 font-medium">
            {poll.title}
          </Link>
        </li>
      ))}
    </ul>
  );
};

export default UserPolls; 