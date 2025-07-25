import { AppBar, Toolbar, Typography, Button, Box } from '@mui/material';
import { Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

const Header = () => {
  const { isAuthenticated, user, logout } = useAuth();

  return (
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
          <Link to="/" style={{ color: 'white', textDecoration: 'none' }}>
            Task Manager
          </Link>
        </Typography>
        {isAuthenticated ? (
          <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <Typography variant="body1" sx={{ mr: 2 }}>
              Welcome, {user?.name || user?.username}
            </Typography>
            <Button color="inherit" onClick={logout}>
              Logout
            </Button>
          </Box>
        ) : (
          <Box>
            <Button 
              color="inherit" 
              component={Link} 
              to="/login"
              sx={{ mr: 1 }}
            >
              Login
            </Button>
            <Button 
              color="inherit" 
              component={Link} 
              to="/register"
            >
              Register
            </Button>
          </Box>
        )}
      </Toolbar>
    </AppBar>
  );
};

export default Header;