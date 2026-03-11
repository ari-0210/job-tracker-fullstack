import apiClient from "@/api/axiosInstance";

export const registerUser = (credentials) => {
  return apiClient.post("/auth/register", credentials);
};

export const loginUser = (credentials) => {
  return apiClient.post("/auth/login", credentials);
};
