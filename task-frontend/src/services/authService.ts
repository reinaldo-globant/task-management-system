import axios from 'axios';

const API_URL = '/api/auth';
const OAUTH2_API_URL = '/api/oauth2';

interface LoginResponse {
  name: string;
  token: string;
  tokenType: string;
  id: number;
  username: string;
  email: string;  
}

interface OAuth2Provider {
  [key: string]: string;
}

interface OAuth2ProvidersResponse {
  providers: OAuth2Provider;
  message: string;
  configured: boolean;
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

const getOAuth2Providers = async (): Promise<OAuth2ProvidersResponse> => {
  const response = await axios.get(`${OAUTH2_API_URL}/providers`);
  return response.data;
};

const initiateOAuth2Login = (_provider: string, providerUrl: string): void => {
  // Redirect to OAuth2 provider
  window.location.href = providerUrl;
};

const handleOAuth2Redirect = (): Promise<{ success: boolean; token?: string; username?: string; error?: string }> => {
  return new Promise((resolve) => {
    // Parse URL parameters
    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get('token');
    const username = urlParams.get('username');
    const error = urlParams.get('error');
    
    if (error) {
      resolve({ success: false, error });
    } else if (token && username) {
      resolve({ success: true, token, username });
    } else {
      resolve({ success: false, error: 'Missing authentication data' });
    }
  });
};

const authService = {
  login,
  register,
  getOAuth2Providers,
  initiateOAuth2Login,
  handleOAuth2Redirect
};

export default authService;