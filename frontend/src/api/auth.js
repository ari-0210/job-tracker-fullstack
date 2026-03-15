import apiClient from "./client";

export const registerUser = (credentials) => {
  return apiClient.post("/auth/register", credentials);
};

export const loginUser = (credentials) => {
  return apiClient.post("/auth/login", credentials);
};
