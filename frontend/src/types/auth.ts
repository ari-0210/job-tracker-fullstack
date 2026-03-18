export interface AuthCredentials {
  username: string;
  password: string;
  //   email?: string;
}

export interface AuthResponse {
  token: string;
  user: {
    id: number;
    username: string;
  };
}
