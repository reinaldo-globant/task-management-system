import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { jwtDecode } from 'jwt-decode';
import authService from '../services/authService';

interface User {
  id: number;
  username: string;
  email: string;
  name: string;
}

interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  login: (username: string, password: string) => Promise<void>;
  loginWithToken: (username: string, token: string) => void;
  register: (username: string, email: string, password: string, name: string) => Promise<void>;
  logout: () => void;
}

export const AuthContext = createContext<AuthContextType | null>(null);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider = ({ children }: AuthProviderProps) => {
  const [user, setUser] = useState<User | null>(null);
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const decoded: any = jwtDecode(token);
        
        // Check if token is expired
        const currentTime = Date.now() / 1000;
        if (decoded.exp && decoded.exp < currentTime) {
          logout();
          return;
        }
        
        const userData = {
          id: decoded.id,
          username: decoded.sub,
          email: decoded.email,
          name: decoded.name || ''
        };
        
        setUser(userData);
        setIsAuthenticated(true);
      } catch (error) {
        console.error('Error decoding token:', error);
        logout();
      }
    }
  }, []);

  const login = async (username: string, password: string) => {
    try {
      const data = await authService.login(username, password);
      
      localStorage.setItem('token', data.token);
      
      setUser({
        id: data.id,
        username: data.username,
        email: data.email,
        name: data.name
      });
      
      setIsAuthenticated(true);
    } catch (error) {
      console.error('Login error:', error);
      throw error;
    }
  };

  const register = async (username: string, email: string, password: string, name: string) => {
    try {
      await authService.register(username, email, password, name);
    } catch (error) {
      console.error('Registration error:', error);
      throw error;
    }
  };

  const loginWithToken = (username: string, token: string) => {
    try {
      localStorage.setItem('token', token);
      
      const decoded: any = jwtDecode(token);
      const userData = {
        id: decoded.id,
        username: username,
        email: decoded.email || '',
        name: decoded.name || username
      };
      
      setUser(userData);
      setIsAuthenticated(true);
    } catch (error) {
      console.error('Error processing OAuth2 token:', error);
      logout();
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    setUser(null);
    setIsAuthenticated(false);
  };

  return (
    <AuthContext.Provider value={{ user, isAuthenticated, login, loginWithToken, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
};