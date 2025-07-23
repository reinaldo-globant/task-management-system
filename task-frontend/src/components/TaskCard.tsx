import { Card, CardContent, Typography, Box, Chip } from '@mui/material';
import { Task } from '../services/taskService';

interface TaskCardProps {
  task: Task;
  onClick: () => void;
}

const TaskCard = ({ task, onClick }: TaskCardProps) => {
  const getStatusColor = (status: string) => {
    switch (status) {
      case 'TODO':
        return 'default';
      case 'IN_PROGRESS':
        return 'primary';
      case 'DONE':
        return 'success';
      default:
        return 'default';
    }
  };

  const getStatusLabel = (status: string) => {
    switch (status) {
      case 'TODO':
        return 'To Do';
      case 'IN_PROGRESS':
        return 'In Progress';
      case 'DONE':
        return 'Done';
      default:
        return status;
    }
  };

  return (
    <Card 
      sx={{ 
        mb: 2, 
        cursor: 'pointer',
        borderLeft: 3,
        borderColor: theme => {
          switch (task.status) {
            case 'TODO': return theme.palette.grey[400];
            case 'IN_PROGRESS': return theme.palette.primary.main;
            case 'DONE': return theme.palette.success.main;
            default: return theme.palette.grey[400];
          }
        },
        '&:hover': {
          boxShadow: 3,
          transform: 'translateY(-2px)',
          transition: 'all 0.2s'
        }
      }} 
      onClick={onClick}
      elevation={1}
    >
      <CardContent>
        <Typography 
          variant="subtitle1" 
          component="div" 
          sx={{ 
            fontWeight: 'medium',
            mb: 1,
            lineHeight: 1.3
          }}
        >
          {task.title}
        </Typography>
        
        {task.description && (
          <Typography 
            variant="body2" 
            color="text.secondary" 
            sx={{ 
              mb: 2,
              overflow: 'hidden',
              textOverflow: 'ellipsis',
              display: '-webkit-box',
              WebkitLineClamp: 2,
              WebkitBoxOrient: 'vertical',
              fontSize: '0.875rem',
              lineHeight: 1.4
            }}
          >
            {task.description}
          </Typography>
        )}
        
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mt: 1 }}>
          <Chip 
            label={getStatusLabel(task.status)} 
            size="small"
            color={getStatusColor(task.status)}
            sx={{ height: 24, fontSize: '0.75rem' }}
          />
          
          {task.updatedAt && (
            <Typography variant="caption" color="text.secondary" sx={{ fontSize: '0.7rem' }}>
              Updated: {new Date(task.updatedAt).toLocaleDateString()}
            </Typography>
          )}
        </Box>
      </CardContent>
    </Card>
  );
};

export default TaskCard;