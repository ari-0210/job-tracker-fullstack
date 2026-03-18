import apiClient from "./client";
import type { AuthCredentials, AuthResponse } from "@/types/auth";
export const registerUser = (credentials: AuthCredentials) => {
  return apiClient.post("/auth/register", credentials);
};

export const loginUser = (credentials: AuthResponse) => {
  return apiClient.post("/auth/login", credentials);
};
