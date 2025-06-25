import { currentUserAtom } from "@/libs/users/atoms/currentUserAtom";
import { useAtomValue } from "jotai";
import styles from "./styles/VoteForm.module.css";
import { FormEventHandler, Fragment, useEffect, useState } from "react";
import Button from "@/components/shared/Button";
import { vote } from "@/libs/api";
import { PollDetailedView } from "@/libs/polls/PollDetailedView";
import { socketAtom } from "@/libs/socket/atoms/socketAtom";
import { StompSubscription } from "@stomp/stompjs";

interface Props {
  initialPoll: PollDetailedView;
}

export default function VoteForm({ initialPoll }: Props) {
  const currentUser = useAtomValue(currentUserAtom);
  const [poll, setPoll] = useState(initialPoll);
  const [choices, setChoices] = useState(initialPoll?.votedOption ?? 0);
  const [selectedCount, setSelectedCount] = useState(0);
  const socket = useAtomValue(socketAtom);

  useEffect(() => {
    if (socket === null) {
      return;
    }

    let subscription: StompSubscription;
    const subscribe = () => {
      subscription = socket.subscribe(
        `/receiver/polls/${initialPoll.id}`,
        (message) => {
          const newPoll: PollDetailedView = JSON.parse(message.body);
          setPoll((oldPoll) => ({
            ...newPoll,
            votedOption: oldPoll.votedOption,
          }));
        },
      );
    };

    if (socket.connected) {
      subscribe();
      return () => subscription.unsubscribe();
    }
    socket.onConnect = () => {
      subscribe();
    };
    return () => subscription.unsubscribe();
  }, [socket, initialPoll]);

  if (!initialPoll) {
    return <></>;
  }

  let totalVoteCount = 0;
  poll.voteOptions.forEach(
    (voteOption) => (totalVoteCount += voteOption.count),
  );

  const onChange = (optionNumber: number) => {
    if (!isMultiple) {
      setChoices(1 << optionNumber);
      setSelectedCount(1);
      return;
    }
    const optionIsAlreadyVoted = (choices & (1 << optionNumber)) !== 0;
    if (!optionIsAlreadyVoted && selectedCount === poll.choiceLimitPerUser) {
      return;
    }
    setChoices((oldChoices) => oldChoices ^ (1 << optionNumber));
    setSelectedCount((value) => (optionIsAlreadyVoted ? value - 1 : value + 1));
  };

  const onSubmit: FormEventHandler<HTMLFormElement> = async (event) => {
    event.preventDefault();
    const response = await vote(poll.id, { value: choices });

    if (!response.success) {
      console.log(response.errorMessage);
      return;
    }
    setPoll((oldPoll) => ({ ...oldPoll, votedOption: choices }));
  };

  const isAuthenticated = currentUser !== null;
  const isMultiple = poll.choiceLimitPerUser > 1;
  const hasVoted = poll.votedOption !== 0;
  const nowDate = Date.now();
  const startDate = Date.parse(poll.startDate);
  const endDate = Date.parse(poll.endDate);
  const activePoll = nowDate >= startDate && nowDate <= endDate;

  return (
    <form onSubmit={onSubmit} className="flex flex-col gap-4 relative">
      {poll.voteOptions.map((opt, i) => (
        <div className="relative flex" key={i}>
          <input
            id={`poll-option-${i}`}
            type={isMultiple ? "checkbox" : "radio"}
            name="poll-option"
            value={i}
            disabled={!isAuthenticated || hasVoted || !activePoll}
            checked={(choices & (1 << i)) !== 0}
            onChange={(e) => onChange(parseInt(e.currentTarget.value))}
            className={styles["vote-option-input"]}
          />
          <label
            htmlFor={`poll-option-${i}`}
            className={styles["vote-option-label"]}
          >
            <span>{opt.name}</span>
            <span className="text-sm">
              {opt.count}{" "}
              {totalVoteCount > 0 &&
                "(" + ((opt.count / totalVoteCount) * 100).toFixed(2) + "%)"}
            </span>
          </label>
        </div>
      ))}
      <Button
        id="vote-button"
        style={{ display: !hasVoted && activePoll ? "block" : "none" }}
        scheme="primary"
        disabled={!isAuthenticated || selectedCount === 0}
        className="w-full"
        variant="outline"
      >
        {isAuthenticated ? "Votar" : "Faça login para votar"}
      </Button>
    </form>
  );
}
