import axios from 'axios';

const API_URL = '/api/auth';

interface LoginResponse {
  name: string;
  token: string;
  tokenType: string;
  id: number;
  username: string;
  email: string;  
}


const login = async (username: string, password: string): Promise<LoginResponse> => {
  const response = await axios.post(`${API_URL}/signin`, {
    username,
    password
  });
  return response.data;
};

const register = async (username: string, email: string, password: string, name: string): Promise<any> => {
  const response = await axios.post(`${API_URL}/signup`, {
    username,
    email,
    password,
    name:name
  });
  return response.data;
};

const authService = {
  login,
  register
};

export default authService;