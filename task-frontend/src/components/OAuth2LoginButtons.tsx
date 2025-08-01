import React, { useState, useEffect } from 'react';
import { Button, Box, CircularProgress, Typography, Stack } from '@mui/material';
import authService from '../services/authService';

interface OAuth2Provider {
  [key: string]: string;
}

interface OAuth2ProvidersResponse {
  providers: OAuth2Provider;
  message: string;
  configured: boolean;
}

const OAuth2LoginButtons: React.FC = () => {
  const [providers, setProviders] = useState<OAuth2Provider>({});
  const [loading, setLoading] = useState(true);
  const [configured, setConfigured] = useState(false);
  const [message, setMessage] = useState('');

  useEffect(() => {
    const fetchProviders = async () => {
      try {
        const response: OAuth2ProvidersResponse = await authService.getOAuth2Providers();
        setProviders(response.providers);
        setConfigured(response.configured);
        setMessage(response.message);
      } catch (error) {
        console.error('Failed to fetch OAuth2 providers:', error);
        setMessage('Unable to load OAuth2 providers');
      } finally {
        setLoading(false);
      }
    };

    fetchProviders();
  }, []);

  const handleOAuth2Login = (provider: string, url: string) => {
    authService.initiateOAuth2Login(provider, url);
  };

  const getProviderIcon = (provider: string) => {
    switch (provider.toLowerCase()) {
      case 'google':
        return '🔍';
      case 'github':
        return '🐙';
      case 'microsoft':
        return '🔷';
      default:
        return '🔐';
    }
  };

  const getProviderName = (provider: string) => {
    return provider.charAt(0).toUpperCase() + provider.slice(1);
  };

  const getProviderColor = (provider: string) => {
    switch (provider.toLowerCase()) {
      case 'google':
        return '#db4437';
      case 'github':
        return '#333';
      case 'microsoft':
        return '#0078d4';
      default:
        return '#666';
    }
  };

  if (loading) {
    return (
      <Box sx={{ textAlign: 'center', py: 2 }}>
        <CircularProgress size={24} />
        <Typography variant="body2" sx={{ mt: 1 }} color="text.secondary">
          Loading providers...
        </Typography>
      </Box>
    );
  }

  if (!configured && !loading) {
    return (
      <Box sx={{ textAlign: 'center', py: 2 }}>
        <Typography variant="body2" color="text.secondary">
          {message || 'OAuth2 providers not configured yet.'}
        </Typography>
        <Typography variant="caption" color="text.secondary" sx={{ mt: 1, display: 'block' }}>
          See SSO_SETUP.md for configuration instructions.
        </Typography>
      </Box>
    );
  }

  return (
    <Stack spacing={1}>
      {Object.entries(providers).map(([provider, url]) => (
        <Button
          key={provider}
          onClick={() => handleOAuth2Login(provider, url)}
          variant="outlined"
          fullWidth
          sx={{
            py: 1.5,
            borderColor: getProviderColor(provider),
            color: getProviderColor(provider),
            '&:hover': {
              borderColor: getProviderColor(provider),
              backgroundColor: `${getProviderColor(provider)}10`,
            },
          }}
          startIcon={
            <span style={{ fontSize: '1.2em' }}>
              {getProviderIcon(provider)}
            </span>
          }
        >
          Continue with {getProviderName(provider)}
        </Button>
      ))}
    </Stack>
  );
};

export default OAuth2LoginButtons;