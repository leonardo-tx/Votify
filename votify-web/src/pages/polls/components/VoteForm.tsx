import VoteOptionView from "@/libs/polls/VoteOptionView";
import { currentUserAtom } from "@/libs/users/atoms/currentUserAtom";
import { useAtomValue } from "jotai";
import styles from "./styles/VoteForm.module.css";
import { FormEventHandler, useState } from "react";
import Button from "@/components/shared/Button";
import { useRouter } from "next/navigation";
import { vote } from "@/libs/api";

interface Props {
  pollId: number;
  voteOptions: VoteOptionView[];
  choiceLimitPerUser: number;
  initialChoices: number;
}

export default function VoteForm({
  pollId,
  voteOptions,
  choiceLimitPerUser,
  initialChoices,
}: Props) {
  const currentUser = useAtomValue(currentUserAtom);
  const router = useRouter();
  const [choices, setChoices] = useState(initialChoices);

  if (!voteOptions) {
    return <></>;
  }

  let totalVoteCount = 0;
  voteOptions.forEach((voteOption) => (totalVoteCount += voteOption.voteCount));

  const onChange = (optionNumber: number) => {
    if (isMultiple) {
      setChoices((oldChoices) => oldChoices ^ (1 << optionNumber));
      return;
    }
    setChoices(1 << optionNumber);
  };

  const onSubmit: FormEventHandler<HTMLFormElement> = async (event) => {
    event.preventDefault();
    const response = await vote(pollId, { value: choices });

    if (response.success) {
      router.refresh();
      return;
    }
    console.log(response.errorMessage);
  };

  const isAuthenticated = currentUser !== null;
  const isMultiple = choiceLimitPerUser > 1;
  const hasVoted = initialChoices !== 0;
  const disabled = !isAuthenticated || hasVoted;

  const getClassNameForLabel = (i: number): string => {
    const labelClassNames = [styles["vote-label"]];
    if (!disabled) labelClassNames.push(styles["vote-label-not-disabled"]);
    if ((choices & (1 << i)) !== 0) {
      labelClassNames.push(styles["vote-label-selected"]);
    }
    return labelClassNames.join(" ");
  };

  return (
    <form onSubmit={onSubmit} className="flex flex-col gap-3">
      {voteOptions.map((opt, i) => (
        <label key={i} className={getClassNameForLabel(i)}>
          <input
            type={isMultiple ? "checkbox" : "radio"}
            name="poll-option"
            value={i}
            disabled={!isAuthenticated || hasVoted}
            checked={(choices & (1 << i)) !== 0}
            onChange={(e) => onChange(parseInt(e.currentTarget.value))}
            className={styles["vote-box"]}
          />
          <span className="flex-1">{opt.name}</span>
          <span className="text-sm">
            {opt.voteCount} (
            {((opt.voteCount / totalVoteCount) * 100).toFixed(2)}%)
          </span>
        </label>
      ))}
      {!hasVoted && (
        <Button
          scheme="primary"
          disabled={!isAuthenticated || choices === 0}
          className="w-full"
          variant="outline"
        >
          {isAuthenticated ? "Votar" : "Faça login para votar"}
        </Button>
      )}
    </form>
  );
}
