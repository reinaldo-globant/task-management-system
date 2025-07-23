import { useState, useEffect } from 'react';
import { 
  Dialog, 
  DialogTitle, 
  DialogContent, 
  DialogActions,
  TextField,
  Button,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Box,
  Typography,
  CircularProgress
} from '@mui/material';
import { Task } from '../services/taskService';

interface TaskDialogProps {
  open: boolean;
  onClose: () => void;
  onSave: (task: Task) => Promise<void>;
  task?: Task | null;
  mode: 'create' | 'edit';
}

const initialTask: Task = {
  title: '',
  description: '',
  status: 'TODO'
};

const TaskDialog = ({ open, onClose, onSave, task, mode }: TaskDialogProps) => {
  const [formData, setFormData] = useState<Task>(initialTask);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (task && mode === 'edit') {
      setFormData(task);
    } else {
      setFormData(initialTask);
    }
  }, [task, mode]);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement> | 
    { target: { name?: string; value: unknown } }
  ) => {
    const { name, value } = e.target;
    if (name) {
      setFormData(prev => ({
        ...prev,
        [name]: value
      }));
    }
  };

  const handleSubmit = async () => {
    if (!formData.title.trim()) {
      setError('Title is required');
      return;
    }
    
    setLoading(true);
    setError(null);
    
    try {
      await onSave(formData);
      onClose();
    } catch (err: any) {
      setError(err.message || 'An error occurred while saving the task');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>
        {mode === 'create' ? 'Create New Task' : 'Edit Task'}
      </DialogTitle>
      
      <DialogContent>
        {error && (
          <Typography color="error" variant="body2" sx={{ mb: 2 }}>
            {error}
          </Typography>
        )}
        
        <Box sx={{ mt: 1 }}>
          <TextField
            margin="dense"
            label="Title"
            type="text"
            fullWidth
            required
            name="title"
            value={formData.title}
            onChange={handleChange}
          />
          
          <TextField
            margin="dense"
            label="Description"
            multiline
            rows={4}
            fullWidth
            name="description"
            value={formData.description}
            onChange={handleChange}
          />
          
          <FormControl fullWidth margin="dense">
            <InputLabel id="status-label">Status</InputLabel>
            <Select
              labelId="status-label"
              name="status"
              value={formData.status}
              label="Status"
              onChange={handleChange}
            >
              <MenuItem value="TODO">To Do</MenuItem>
              <MenuItem value="IN_PROGRESS">In Progress</MenuItem>
              <MenuItem value="DONE">Done</MenuItem>
            </Select>
          </FormControl>
        </Box>
      </DialogContent>
      
      <DialogActions>
        <Button onClick={onClose} color="inherit">
          Cancel
        </Button>
        <Button 
          onClick={handleSubmit} 
          color="primary" 
          variant="contained"
          disabled={loading}
        >
          {loading ? <CircularProgress size={24} /> : mode === 'create' ? 'Create' : 'Save'}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default TaskDialog;