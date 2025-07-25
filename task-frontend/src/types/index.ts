export interface User {
  id: number;
  username: string;
  email: string;
  name: string;
}

export interface LoginResponse {
  accessToken: string;
  tokenType: string;
  id: number;
  username: string;
  email: string;
  name: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  name: string;
}