import { Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import TaskBoardPage from './pages/TaskBoardPage';
import Header from './components/Header';
import OAuth2RedirectHandler from './components/OAuth2RedirectHandler';
import { useAuth } from './contexts/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';

const theme = createTheme({
  palette: {
    primary: {
      main: '#2196f3',
    },
    secondary: {
      main: '#f50057',
    },
  },
});

function App() {
  const { isAuthenticated } = useAuth();

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Header />
      <Routes>
        <Route path="/login" element={!isAuthenticated ? <LoginPage /> : <Navigate to="/tasks" />} />
        <Route path="/register" element={!isAuthenticated ? <RegisterPage /> : <Navigate to="/tasks" />} />
        <Route path="/oauth2/redirect" element={<OAuth2RedirectHandler />} />
        <Route 
          path="/tasks" 
          element={
            <ProtectedRoute>
              <TaskBoardPage />
            </ProtectedRoute>
          } 
        />
        <Route path="/" element={<Navigate to={isAuthenticated ? "/tasks" : "/login"} />} />
      </Routes>
    </ThemeProvider>
  );
}

export default App;