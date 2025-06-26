export interface PollInsertDTO {
  title: string;
  description: string;
  startDate: string;
  endDate: string;
  choiceLimitPerUser: number;
  voteOptions: string[];
} 