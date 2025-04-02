import axios from "axios";
import ApiResponse from "./ApiResponse";
import UserQueryView from "./users/UserQueryView";

export const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE_URL,
  withCredentials: true,
});

export const getUserById = async (
  id: number,
): Promise<ApiResponse<UserQueryView | null>> => {
  try {
    const { data } = await api.get<ApiResponse<UserQueryView>>(`/users/${id}`);
    return data;
  } catch (error: any) {
    return error.response;
  }
};
