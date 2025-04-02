import VotifyErrorCode from "./VotifyErrorCode";

export default interface ApiResponse<T> {
  success: boolean;
  data: T;
  errorCode: VotifyErrorCode;
  errorMessage: string;
}
