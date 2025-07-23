import { Box, Typography, Paper } from '@mui/material';
import TaskCard from './TaskCard';
import { Task } from '../services/taskService';

interface TaskColumnProps {
  title: string;
  tasks: Task[];
  status: 'TODO' | 'IN_PROGRESS' | 'DONE';
  onTaskClick: (task: Task) => void;
}

const TaskColumn = ({ title, tasks, status, onTaskClick }: TaskColumnProps) => {
  const getColumnBgColor = (columnStatus: 'TODO' | 'IN_PROGRESS' | 'DONE') => {
    switch (columnStatus) {
      case 'TODO': 
        return 'rgba(0, 0, 0, 0.02)';
      case 'IN_PROGRESS': 
        return 'rgba(3, 169, 244, 0.05)';
      case 'DONE': 
        return 'rgba(76, 175, 80, 0.05)';
      default:
        return 'rgba(0, 0, 0, 0.02)';
    }
  };

  const getColumnBorderColor = (columnStatus: 'TODO' | 'IN_PROGRESS' | 'DONE') => {
    switch (columnStatus) {
      case 'TODO': 
        return (theme: any) => theme.palette.grey[300];
      case 'IN_PROGRESS': 
        return (theme: any) => theme.palette.primary.light;
      case 'DONE': 
        return (theme: any) => theme.palette.success.light;
      default:
        return (theme: any) => theme.palette.grey[300];
    }
  };

  return (
    <Paper 
      elevation={1} 
      sx={{ 
        width: 320, 
        minHeight: 500, 
        p: 2,
        backgroundColor: getColumnBgColor(status),
        borderTop: 3,
        borderColor: getColumnBorderColor(status),
        borderRadius: '4px',
      }}
    >
      <Typography 
        variant="h6" 
        component="h2" 
        sx={{ 
          mb: 2, 
          pb: 1,
          textAlign: 'center',
          borderBottom: 1,
          borderColor: 'divider',
          fontWeight: 'medium',
          color: status === 'TODO' 
            ? 'text.primary' 
            : status === 'IN_PROGRESS' 
              ? 'primary.dark' 
              : 'success.dark'
        }}
      >
        {title} <Typography component="span" color="text.secondary" variant="body2">({tasks.length})</Typography>
      </Typography>
      
      <Box sx={{ overflow: 'auto', maxHeight: 'calc(100vh - 200px)' }}>
        {tasks.length > 0 ? (
          tasks.map(task => (
            <TaskCard 
              key={task.id} 
              task={task} 
              onClick={() => onTaskClick(task)} 
            />
          ))
        ) : (
          <Typography 
            variant="body2" 
            color="text.secondary" 
            sx={{ textAlign: 'center', mt: 2, px: 2 }}
          >
            No tasks in this column. Click "New Task" to create one.
          </Typography>
        )}
      </Box>
    </Paper>
  );
};

export default TaskColumn;