import { api } from "@/libs/api";
import ApiResponse from "@/libs/ApiResponse";
import useSWR from "swr";
import UserQueryView from "../UserQueryView";

export function useUser(userId: number) {
  const { data, error } = useSWR<ApiResponse<UserQueryView>, ApiResponse<null>>(
    `/users/${userId}`,
    api,
    {
      revalidateOnFocus: false,
    },
  );

  return {
    user: data?.data,
    isLoading: !error && !data,
  };
}
