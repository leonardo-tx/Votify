import PollList from "@/pages/home/components/PollList";
import UserSettings from "./UserSettings";
import PollSimpleView from "@/libs/polls/PollSimpleView";
import Link from "next/link";

interface UserData {
  id: number;
  username: string;
  name: string;
  polls: PollSimpleView[];
}

interface UserProfileProps {
  user: UserData;
  isAuthenticatedUserProfile: boolean;
}

const UserProfile: React.FC<UserProfileProps> = ({
  user,
  isAuthenticatedUserProfile,
}) => {
  return (
    <div className="min-h-screen py-8">
      <div className="container mx-auto px-4 sm:px-6 lg:px-8 max-w-3xl space-y-8">
        <div className="shadow-xl rounded-lg p-6 md:p-8">
          <div className="text-center">
            <h1 id="user-profile-name" className="text-4xl font-bold text-gray-800">
              {user.name}
            </h1>
            <p id="user-profile-username" className="text-xl mt-1 text-gray-600">
              @{user.username}
            </p>
          </div>
        </div>

        <section className="shadow-lg rounded-lg p-6 md:p-8">
          <h2
            id="user-profile-created-polls-title"
            className="text-2xl font-semibold text-gray-700 mb-4"
          >
            Enquetes Criadas
          </h2>
          {user.polls.length > 0 ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {user.polls.map((poll) => (
                <Link
                  key={poll.id}
                  href={`/poll/${poll.id}`}
                  id={`poll-card-${poll.id}`}
                  className="block bg-gray-50 p-4 rounded-lg shadow hover:shadow-md transition-shadow duration-200"
                >
                  <h3 className="text-lg font-medium text-blue-600 truncate">
                    {poll.title}
                  </h3>
                </Link>
              ))}
            </div>
          ) : (
            <p id="user-profile-no-polls-message" className="text-gray-600">
              Você ainda não criou nenhuma enquete.
            </p>
          )}
        </section>

        {isAuthenticatedUserProfile && (
          <section className="shadow-lg rounded-lg p-6 md:p-8">
            <h2 className="text-2xl font-semibold mb-6 border-b pb-3">
              Configurações da Conta
            </h2>
            <UserSettings />
          </section>
        )}
      </div>
    </div>
  );
};

export default UserProfile;
