import axios from "axios";
import ApiResponse from "./ApiResponse";
import UserQueryView from "./users/UserQueryView";
import UserLoginDTO from "./users/UserLoginDTO";
import UserDetailedView from "./users/UserDetailedView";
import VotifyErrorCode from "./VotifyErrorCode";

export const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE_URL,
  withCredentials: true,
});

export const getUserById = async (
  id: number,
): Promise<ApiResponse<UserQueryView | null>> => {
  return await commonRequester(async () => {
    const { data } = await api.get<ApiResponse<UserQueryView>>(`/users/${id}`);
    return data;
  });
};

export const getCurrentUser = async (): Promise<
  ApiResponse<UserDetailedView | null>
> => {
  return await commonRequester(async () => {
    const { data } = await api.get<ApiResponse<UserDetailedView>>("/users/me");
    return data;
  });
};

export const logout = async (): Promise<ApiResponse<null>> => {
  return await commonRequester(async () => {
    const { data } = await api.post<ApiResponse<null>>("/auth/logout");
    return data;
  });
};

export const login = async (
  credentials: UserLoginDTO,
): Promise<ApiResponse<UserLoginDTO | null>> => {
  return await commonRequester(async () => {
    const { data } = await api.post<ApiResponse<UserLoginDTO>>(
      "/auth/login",
      credentials,
    );
    return data;
  });
};

const commonRequester = async <T>(
  request: () => Promise<ApiResponse<T | null>>,
) => {
  try {
    return await request();
  } catch (error: any) {
    if (error.response === undefined) return getBackupErrorObject();
    if (
      error.response.data.errorCode === VotifyErrorCode.ACCESS_TOKEN_EXPIRED ||
      error.response.data.errorCode === VotifyErrorCode.COMMON_UNAUTHORIZED
    ) {
      return await refreshAndDoRequestAgain(request);
    }
    return error.response.data;
  }
};

const refreshAndDoRequestAgain = async <T>(
  request: () => Promise<ApiResponse<T | null>>,
) => {
  try {
    await api.post<ApiResponse<null>>("/auth/refresh-tokens");
    return await request();
  } catch (error: any) {
    if (error.response === undefined) return getBackupErrorObject();
    return error.response.data;
  }
};

const getBackupErrorObject = (): ApiResponse<null> => ({
  success: false,
  data: null,
  errorCode: VotifyErrorCode.CLIENT_CONNECTION_FAILED,
  errorMessage: "It was not possible to make a request to the API.",
});
