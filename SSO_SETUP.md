# SSO Integration Setup Guide

This guide explains how to set up Single Sign-On (SSO) with OAuth2 providers in the Task Management System.

## 🎯 Current Status

The SSO integration is **ready to use** but requires OAuth2 provider configuration. 

- ✅ **Frontend**: OAuth2 login buttons are visible on login/register pages
- ✅ **Backend**: OAuth2 endpoints and security configuration ready
- ⚠️ **Configuration**: Requires OAuth2 client IDs/secrets to be functional

## Supported Providers

- **Google OAuth2**
- **GitHub OAuth2** 
- **Microsoft OAuth2/Azure AD**

## Quick Start (Without Configuration)

1. Start the application: `docker-compose up --build`
2. Navigate to: `http://localhost:3000/login`
3. You'll see: OAuth2 section with message "OAuth2 providers not configured yet"
4. Follow the setup instructions below to enable OAuth2 providers

## Setup Instructions

### 1. Google OAuth2 Setup

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable the Google+ API
4. Go to "Credentials" → "Create Credentials" → "OAuth 2.0 Client IDs"
5. Set Application type to "Web application"
6. Add authorized redirect URIs:
   - `http://localhost:8081/login/oauth2/code/google` (for development)
   - `https://yourdomain.com/login/oauth2/code/google` (for production)
7. Copy Client ID and Client Secret

### 2. GitHub OAuth2 Setup

1. Go to [GitHub Developer Settings](https://github.com/settings/developers)
2. Click "New OAuth App"
3. Fill in the application details:
   - Authorization callback URL: `http://localhost:8081/login/oauth2/code/github`
4. Copy Client ID and Client Secret

### 3. Microsoft OAuth2 Setup

1. Go to [Azure Portal](https://portal.azure.com/)
2. Navigate to "Azure Active Directory" → "App registrations" → "New registration"
3. Set Redirect URI to: `http://localhost:8081/login/oauth2/code/microsoft`
4. Go to "Certificates & secrets" → "New client secret"
5. Copy Application (client) ID and Client Secret

### 4. Environment Configuration

**Option A: Using Docker Compose (Recommended)**

Edit the `docker-compose.yml` file and add environment variables to the `user-service` section:

```yaml
user-service:
  # ... existing configuration
  environment:
    # ... existing environment variables
    
    # Google OAuth2 (optional - only configure if you want Google login)
    - GOOGLE_CLIENT_ID=your-google-client-id.apps.googleusercontent.com
    - GOOGLE_CLIENT_SECRET=your-google-client-secret
    
    # GitHub OAuth2 (optional - only configure if you want GitHub login)
    - GITHUB_CLIENT_ID=your-github-client-id
    - GITHUB_CLIENT_SECRET=your-github-client-secret
    
    # Microsoft OAuth2 (optional - only configure if you want Microsoft login)
    - MICROSOFT_CLIENT_ID=your-microsoft-client-id
    - MICROSOFT_CLIENT_SECRET=your-microsoft-client-secret
```

**Option B: Using .env file**

Create a `.env` file in the project root:

```bash
# Copy example file
cp .env.example .env

# Edit with your OAuth2 credentials
nano .env
```

Update the OAuth2 credentials:

```bash
# Google OAuth2 (leave empty if not configuring)
GOOGLE_CLIENT_ID=your-google-client-id.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=your-google-client-secret

# GitHub OAuth2 (leave empty if not configuring)
GITHUB_CLIENT_ID=your-github-client-id
GITHUB_CLIENT_SECRET=your-github-client-secret

# Microsoft OAuth2 (leave empty if not configuring)
MICROSOFT_CLIENT_ID=your-microsoft-client-id
MICROSOFT_CLIENT_SECRET=your-microsoft-client-secret
```

**Note**: You can configure one, two, or all three providers. The system will only show login buttons for configured providers.

### 5. Frontend Integration

The frontend now includes OAuth2 login buttons that will appear on the login page. Users can:

1. Click on any OAuth2 provider button
2. Get redirected to the provider's authentication page
3. After successful authentication, get redirected back with a JWT token
4. Automatically logged into the application

### 6. Database Schema Updates

The User model now includes additional fields for OAuth2:

- `provider` - Authentication provider (LOCAL, GOOGLE, GITHUB, MICROSOFT)
- `provider_id` - Unique ID from the OAuth2 provider
- `image_url` - Profile image URL from the provider

### 7. API Endpoints

New OAuth2 endpoints:

- `GET /api/oauth2/providers` - Get available OAuth2 providers
- `GET /oauth2/authorization/{provider}` - Initiate OAuth2 login
- `GET /oauth2/redirect` - Handle OAuth2 callback

### 8. Testing

1. Start the services: `docker-compose up --build`
2. Navigate to `http://localhost:3000/login`
3. You should see OAuth2 login buttons below the regular login form
4. Click on a provider to test the OAuth2 flow

## Security Considerations

1. **HTTPS in Production**: Always use HTTPS in production environments
2. **Secure Secrets**: Store OAuth2 client secrets securely (environment variables, secrets management)
3. **Redirect URI Validation**: Ensure redirect URIs are properly configured in OAuth2 providers
4. **CORS Configuration**: Update CORS settings for production domains
5. **Token Expiration**: Configure appropriate JWT token expiration times

## Troubleshooting

### Common Issues

1. **Invalid Redirect URI**: Ensure redirect URIs match exactly in OAuth2 provider settings
2. **CORS Errors**: Check CORS configuration in WebSecurityConfig
3. **Token Issues**: Verify JWT secret and expiration settings
4. **Provider Not Working**: Check client ID/secret and provider-specific scopes

### Debug Mode

Enable debug logging by setting:
```properties
logging.level.org.springframework.security=DEBUG
```

## Production Deployment

For production deployment:

1. Update redirect URIs to use your production domain
2. Use secure environment variable management
3. Enable HTTPS
4. Update CORS configuration for your frontend domain
5. Use production-grade JWT secrets

## Integration with Existing Authentication

The SSO implementation works alongside existing username/password authentication. Users can:

- Create accounts using traditional signup
- Login using username/password
- Login using OAuth2 providers
- Link OAuth2 accounts to existing accounts (email matching)

Users authenticated via OAuth2 will have the same permissions and access as regular users, with roles assigned automatically based on configuration.