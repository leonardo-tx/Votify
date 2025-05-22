import PollList from "@/pages/home/components/PollList";
import UserSettings from "./UserSettings";
import PollSimpleView from "@/libs/polls/PollSimpleView";

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
            <h1 className="text-4xl font-bold">{user.name}</h1>
            <p className="text-xl mt-1">@{user.username}</p>
          </div>
        </div>

        <section className="shadow-lg rounded-lg p-6 md:p-8">
          <h2 className="text-2xl font-semibold mb-6 border-b pb-3">
            Enquetes Criadas
          </h2>
          <PollList
            showUser={false}
            polls={user.polls.map((poll) => ({ poll: poll, user: null }))}
          />
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
