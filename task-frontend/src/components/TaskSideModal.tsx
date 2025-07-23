import { 
  Box, 
  Typography, 
  Drawer, 
  IconButton, 
  Divider, 
  Button,
  Chip,
  TextField,
  MenuItem,
  Select,
  FormControl,
  InputLabel,
  CircularProgress,
  Stack
} from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import DeleteIcon from '@mui/icons-material/Delete';
import { useState, useEffect } from 'react';
import { Task } from '../services/taskService';

interface TaskSideModalProps {
  open: boolean;
  task: Task | null;
  onClose: () => void;
  onSave: (task: Task) => Promise<void>;
  onDelete: () => void;
}

const TaskSideModal = ({ open, task, onClose, onSave, onDelete }: TaskSideModalProps) => {
  const [editedTask, setEditedTask] = useState<Task | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (task) {
      // Edit existing task
      setEditedTask({ ...task });
    } else {
      // Create new task with default values
      setEditedTask({
        title: '',
        description: '',
        status: 'TODO'
      });
    }
    setError(null);
  }, [task, open]);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement> | 
    { target: { name?: string; value: unknown } }
  ) => {
    if (!editedTask) return;
    
    const { name, value } = e.target;
    if (name) {
      setEditedTask(prev => prev ? ({
        ...prev,
        [name]: value
      }) : null);
    }
  };

  const handleSave = async () => {
    if (!editedTask) return;
    
    if (!editedTask.title.trim()) {
      setError('Title is required');
      return;
    }
    
    setLoading(true);
    setError(null);
    
    try {
      await onSave(editedTask);
      onClose();
    } catch (err: any) {
      setError(err.message || 'Failed to save task');
    } finally {
      setLoading(false);
    }
  };

  // Status helpers defined here for future use
  // They are currently used in the MenuItem elements

  return (
    <Drawer
      anchor="right"
      open={open}
      onClose={onClose}
      PaperProps={{
        sx: {
          width: { xs: '100%', sm: 400 },
          padding: 3,
          display: 'flex',
          flexDirection: 'column',
          height: '100%'
        }
      }}
      ModalProps={{
        BackdropProps: {
          sx: {
            backgroundColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }}
    >
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
        <Typography variant="h6">
          {task ? 'Task Details' : 'New Task'}
        </Typography>
        <IconButton edge="end" color="inherit" onClick={onClose} aria-label="close">
          <CloseIcon />
        </IconButton>
      </Box>
      
      <Divider sx={{ mb: 3 }} />
      
      {editedTask && (
        <Stack spacing={3} sx={{ flexGrow: 1, overflow: 'auto' }}>
          {error && (
            <Typography color="error" variant="body2">
              {error}
            </Typography>
          )}
          
          <TextField
            label="Title"
            name="title"
            value={editedTask.title}
            onChange={handleChange}
            fullWidth
            required
            error={error === 'Title is required'}
            helperText={error === 'Title is required' ? 'Title is required' : ''}
          />
          
          <TextField
            label="Description"
            name="description"
            value={editedTask.description}
            onChange={handleChange}
            multiline
            rows={4}
            fullWidth
          />
          
          <FormControl fullWidth>
            <InputLabel id="status-label">Status</InputLabel>
            <Select
              labelId="status-label"
              name="status"
              value={editedTask.status}
              label="Status"
              onChange={handleChange}
            >
              <MenuItem value="TODO">To Do</MenuItem>
              <MenuItem value="IN_PROGRESS">In Progress</MenuItem>
              <MenuItem value="DONE">Done</MenuItem>
            </Select>
          </FormControl>
          
          {task?.id && (
            <Box sx={{ mt: 2 }}>
              <Chip 
                label={`ID: ${task.id}`} 
                size="small" 
                sx={{ mr: 1 }} 
              />
              {task.createdAt && (
                <Chip 
                  label={`Created: ${new Date(task.createdAt).toLocaleDateString()}`} 
                  size="small" 
                  sx={{ mr: 1 }} 
                />
              )}
              {task.updatedAt && (
                <Chip 
                  label={`Updated: ${new Date(task.updatedAt).toLocaleDateString()}`} 
                  size="small" 
                />
              )}
            </Box>
          )}
        </Stack>
      )}
      
      <Box sx={{ mt: 'auto', pt: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        {task?.id && (
          <Button 
            startIcon={<DeleteIcon />} 
            color="error" 
            onClick={onDelete}
            disabled={loading}
          >
            Delete
          </Button>
        )}
        
        <Box sx={{ flexGrow: 1 }} />
        
        <Button 
          color="inherit" 
          onClick={onClose} 
          sx={{ mr: 1 }}
          disabled={loading}
        >
          Cancel
        </Button>
        
        <Button 
          variant="contained" 
          color="primary" 
          onClick={handleSave}
          disabled={loading}
        >
          {loading ? <CircularProgress size={24} /> : 'Save'}
        </Button>
      </Box>
    </Drawer>
  );
};

export default TaskSideModal;