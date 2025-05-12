import axios from "axios";
import ApiResponse from "./ApiResponse";
import UserQueryView from "./users/UserQueryView";
import UserLoginDTO from "./users/UserLoginDTO";
import UserDetailedView from "./users/UserDetailedView";
import VotifyErrorCode from "./VotifyErrorCode";
import PollSimpleView from "./polls/PollSimpleView";
import { PageResponse } from "./PageResponse";
import UserPasswordResetRequestDto from "@/libs/users/UserPasswordResetRequestDto";
import UserPasswordResetResponseDto from "@/libs/users/UserPasswordResetResponseDto";
import UserPasswordResetConfirmDTO from "@/libs/users/UserPasswordResetConfirmDTO";
import { PollDetailedView } from "./polls/PollDetailedView";
import VoteInsertDTO from "./polls/VoteInsertDTO";
import EmailConfirmationRequestDTO from "./users/EmailConfirmationRequestDTO";

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

export const forgotPassword = async (
    request: UserPasswordResetRequestDto,
): Promise<ApiResponse<UserPasswordResetResponseDto | null>> => {
  return await commonRequester(async () => {
    const { data } = await api.post<ApiResponse<UserPasswordResetResponseDto>>(
        "/auth/forgot-password",
        request,
    );
    return data;
  });
};

export const resetPassword = async (
    request: UserPasswordResetConfirmDTO,
): Promise<ApiResponse<UserPasswordResetConfirmDTO | null>> => {
  return await commonRequester(async () => {
    const { data } = await api.post<ApiResponse<null>>(
        "/auth/reset-password",
        request,
    );
    return data;
  });
};

export async function confirmEmail(request: EmailConfirmationRequestDTO): Promise<ApiResponse<null>> {
    return await commonRequester(async () => {
    const { data } = await api.post<ApiResponse<null>>(
      `/auth/confirm-email`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(request)
    });
    return data;
  });
}

export const forgotPassword = async (
    request: UserPasswordResetRequestDto,
): Promise<ApiResponse<UserPasswordResetResponseDto | null>> => {
  return await commonRequester(async () => {
    const { data } = await api.post<ApiResponse<UserPasswordResetResponseDto>>(
        "/auth/forgot-password",
        request,
    );
    return data;
  });
};

export const resetPassword = async (
    request: UserPasswordResetConfirmDTO,
): Promise<ApiResponse<UserPasswordResetConfirmDTO | null>> => {
  return await commonRequester(async () => {
    const { data } = await api.post<ApiResponse<null>>(
        "/auth/reset-password",
        request,
    );
    return data;
  });
};



export const getMyPolls = async (
  page: number = 0,
  size: number = 10,
): Promise<ApiResponse<PageResponse<PollSimpleView> | null>> => {
  return await commonRequester(async () => {
    const { data } = await api.get<ApiResponse<PageResponse<PollSimpleView>>>(
      `/polls/me?page=${page}&size=${size}`,
    );
    return data;
  });
};

export const searchPollsByTitle = async (
  title: string,
  page: number = 0,
  size: number = 10,
): Promise<ApiResponse<PageResponse<PollSimpleView> | null>> => {
  return await commonRequester(async () => {
    const { data } = await api.get<ApiResponse<PageResponse<PollSimpleView>>>(
      `/polls/search?title=${encodeURIComponent(title)}&page=${page}&size=${size}`,
    );
    return data;
  });
};

export const getPollById = async (
  id: number,
): Promise<ApiResponse<PollDetailedView | null>> => {
  return await commonRequester(async () => {
    const { data } = await api.get<ApiResponse<PollDetailedView>>(
      `/polls/${id}`,
    );
    return data;
  });
};

export const getAllActivePolls = async (
  page: number = 0,
  size: number = 10,
): Promise<ApiResponse<PageResponse<PollSimpleView> | null>> => {
  return await commonRequester(async () => {
    const { data } = await api.get<ApiResponse<PageResponse<PollSimpleView>>>(
      `/polls/active?page=${page}&size=${size}`,
    );
    return data;
  });
};

export const vote = async (
  id: number,
  voteOption: VoteInsertDTO,
): Promise<ApiResponse<number | null>> => {
  return await commonRequester(async () => {
    const { data } = await api.post<ApiResponse<number>>(
      `/polls/${id}/vote`,
      voteOption,
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
