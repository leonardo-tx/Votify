import UserPolls from './UserPolls';
import UserSettings from './UserSettings';

interface UserData {
  id: number; 
  username: string; 
  name: string;
  polls: Array<{ 
    id: number; 
    title: string; 
    description?: string | null;
    startDate?: string | null;
    endDate?: string | null;
  }>;
}

interface UserProfileProps {
  user: UserData;
  isAuthenticatedUserProfile: boolean;
}

const UserProfile: React.FC<UserProfileProps> = ({ user, isAuthenticatedUserProfile }) => {
  return (
    <div className="min-h-screen bg-slate-50 py-8">
      <div className="container mx-auto px-4 sm:px-6 lg:px-8 max-w-3xl space-y-8">
        
        <div className="bg-white shadow-xl rounded-lg p-6 md:p-8">
          <div className="text-center">
            <h1 className="text-4xl font-bold text-slate-800">{user.name}</h1>
            <p className="text-xl text-slate-500 mt-1">@{user.username}</p>
          </div>
        </div>

        <section className="bg-white shadow-lg rounded-lg p-6 md:p-8">
          <h2 className="text-2xl font-semibold text-slate-700 mb-6 border-b pb-3">Enquetes Criadas</h2>
          <UserPolls polls={user.polls} />
        </section>

        {isAuthenticatedUserProfile && (
          <section className="bg-white shadow-lg rounded-lg p-6 md:p-8">
            <h2 className="text-2xl font-semibold text-slate-700 mb-6 border-b pb-3">Configurações da Conta</h2>
            <UserSettings />
          </section>
        )}
      </div>
    </div>
  );
};

export default UserProfile; 