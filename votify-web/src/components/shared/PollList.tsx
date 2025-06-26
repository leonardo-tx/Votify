import PollSimpleView from "@/libs/polls/PollSimpleView";
import UserQueryView from "@/libs/users/UserQueryView";
import PollCard from "./PollCard";
import { useEffect, useState } from "react";

interface Props {
  polls?: { poll: PollSimpleView; user: UserQueryView | null }[];
  showUser?: boolean;
}

export default function PollList({ polls: initialPolls, showUser = true }: Props) {
  const [polls, setPolls] = useState(initialPolls ?? []);
  const [now, setNow] = useState(0);

  useEffect(() => {
    setNow(Date.now());
    const timer = setInterval(() => {
      setNow(Date.now());
    }, 1000);

    return () => clearInterval(timer);
  }, []);

  const handleCancel = (pollId: number) => {
    setPolls((prev) => prev.filter((item) => item.poll.id !== pollId));
  };

  return (
      <div id="poll-list" className="grid grid-cols-1 gap-4">
        {polls.map((item) => (
            <PollCard
                key={item.poll.id}
                poll={item.poll}
                user={item.user}
                now={now}
                showUser={showUser}
                onCancel={handleCancel}
            />
        ))}
      </div>
  );
}
