import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import authService from '../services/authService';

const OAuth2RedirectHandler: React.FC = () => {
  const navigate = useNavigate();
  const { loginWithToken } = useAuth();

  useEffect(() => {
    const handleOAuth2Callback = async () => {
      try {
        const result = await authService.handleOAuth2Redirect();
        
        if (result.success && result.token && result.username) {
          // Update auth context with OAuth2 token
          loginWithToken(result.username, result.token);
          
          navigate('/dashboard');
        } else {
          console.error('OAuth2 authentication failed:', result.error);
          navigate('/login?error=' + encodeURIComponent(result.error || 'Authentication failed'));
        }
      } catch (error) {
        console.error('OAuth2 callback error:', error);
        navigate('/login?error=' + encodeURIComponent('Authentication error'));
      }
    };

    handleOAuth2Callback();
  }, [navigate, loginWithToken]);

  return (
    <div className="flex items-center justify-center min-h-screen">
      <div className="text-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
        <p className="mt-4 text-gray-600">Processing authentication...</p>
      </div>
    </div>
  );
};

export default OAuth2RedirectHandler;