import VoteOptionView from "./VoteOptionView";

export interface PollDetailedView {
  id: number;
  title: string;
  description: string;
  startDate: string;
  endDate: string;
  choiceLimitPerUser: number;
  responsibleId: number;
  voteOptions: VoteOptionView[];
  votedOption: number;
  userRegistration: boolean;
}
