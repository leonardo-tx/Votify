import axios from "axios";
import ApiResponse from "./ApiResponse";
import UserQueryView from "./users/UserQueryView";
import UserLoginView from "./users/UserLoginView";

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

export const getCurrentUser = async (): Promise<ApiResponse<UserQueryView | null>> => {
  try {
    const { data } = await api.get<ApiResponse<UserQueryView>>('/users/me');
    return data;
  } catch (error: any) {
    return error.response;
  }
};

export const logout = async (): Promise<ApiResponse<null>> => {
  try {
    const { data } = await api.post<ApiResponse<null>>('/auth/logout');
    return data;
  } catch (error: any) {
    return error.response;
  }
};

export const login = async (
  credentials: UserLoginView
): Promise<ApiResponse<UserLoginView | null>> => {
  try {
    const { data } = await api.post<ApiResponse<UserLoginView>>('/auth/login', credentials);
    return data;
  } catch (error: any) {
    return error.response;
  }
};
