import { useState, useEffect } from 'react';
import { 
  Container, 
  Box, 
  Typography, 
  Button, 
  CircularProgress, 
  Alert,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import TaskColumn from '../components/TaskColumn';
import TaskSideModal from '../components/TaskSideModal';
import taskService, { Task } from '../services/taskService';

const TaskBoardPage = () => {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [openDialog, setOpenDialog] = useState(false);
  const [selectedTask, setSelectedTask] = useState<Task | null>(null);
  const [dialogMode, setDialogMode] = useState<'create' | 'edit'>('create');
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);

  useEffect(() => {
    fetchTasks();
  }, []);

  const fetchTasks = async () => {
    setLoading(true);
    setError(null);
    
    try {
      const data = await taskService.getMyTasks();
      setTasks(data);
    } catch (err: any) {
      setError('Failed to load tasks. Please try again.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateTask = () => {
    setSelectedTask(null);
    setDialogMode('create');
    setOpenDialog(true);
  };

  const handleEditTask = (task: Task) => {
    setSelectedTask(task);
    setDialogMode('edit');
    setOpenDialog(true);
  };

  const handleDeleteTask = () => {
    if (selectedTask?.id) {
      setOpenDeleteDialog(true);
    }
  };

  const confirmDeleteTask = async () => {
    if (selectedTask?.id) {
      try {
        await taskService.deleteTask(selectedTask.id);
        setTasks(prev => prev.filter(task => task.id !== selectedTask.id));
        setOpenDeleteDialog(false);
        setOpenDialog(false);
      } catch (err) {
        console.error('Failed to delete task:', err);
      }
    }
  };

  const handleSaveTask = async (task: Task) => {
    try {
      if (dialogMode === 'create') {
        const newTask = await taskService.createTask(task);
        setTasks(prev => [...prev, newTask]);
      } else {
        if (selectedTask?.id) {
          const updatedTask = await taskService.updateTask(selectedTask.id, task);
          setTasks(prev => 
            prev.map(t => (t.id === updatedTask.id ? updatedTask : t))
          );
        }
      }
    } catch (err) {
      console.error('Failed to save task:', err);
      throw err;
    }
  };

  const todoTasks = tasks.filter(task => task.status === 'TODO');
  const inProgressTasks = tasks.filter(task => task.status === 'IN_PROGRESS');
  const doneTasks = tasks.filter(task => task.status === 'DONE');

  return (
    <Box sx={{ height: 'calc(100vh - 64px)', display: 'flex', flexDirection: 'column' }}>
      <Container maxWidth="xl" sx={{ flexGrow: 1, display: 'flex', flexDirection: 'column', py: 3 }}>
        <Box sx={{ 
          mb: 3, 
          display: 'flex', 
          justifyContent: 'space-between', 
          alignItems: 'center',
          px: 1
        }}>
          <Typography variant="h4" component="h1" fontWeight="500">
            My Task Board
          </Typography>
          
          <Button 
            variant="contained" 
            startIcon={<AddIcon />} 
            onClick={handleCreateTask}
            size="large"
          >
            New Task
          </Button>
        </Box>
        
        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', flexGrow: 1 }}>
            <CircularProgress />
          </Box>
        ) : error ? (
          <Alert severity="error" sx={{ mt: 2 }}>
            {error}
          </Alert>
        ) : (
          <Box 
            sx={{ 
              display: 'flex', 
              gap: 3,
              overflowX: 'auto',
              pb: 2,
              flexGrow: 1
            }}
          >
          <TaskColumn 
            title="To Do" 
            tasks={todoTasks} 
            status="TODO" 
            onTaskClick={handleEditTask} 
          />
          
          <TaskColumn 
            title="In Progress" 
            tasks={inProgressTasks} 
            status="IN_PROGRESS" 
            onTaskClick={handleEditTask} 
          />
          
          <TaskColumn 
            title="Done" 
            tasks={doneTasks} 
            status="DONE" 
            onTaskClick={handleEditTask} 
          />
        </Box>
      )}
      
      <TaskSideModal 
        open={openDialog}
        onClose={() => setOpenDialog(false)}
        onSave={handleSaveTask}
        task={selectedTask}
        onDelete={handleDeleteTask}
      />
      
      <Dialog
        open={openDeleteDialog}
        onClose={() => setOpenDeleteDialog(false)}
      >
        <DialogTitle>Delete Task</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to delete this task? This action cannot be undone.
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDeleteDialog(false)} color="inherit">
            Cancel
          </Button>
          <Button onClick={confirmDeleteTask} color="error" autoFocus>
            Delete
          </Button>
        </DialogActions>
      </Dialog>
      </Container>
    </Box>
  );
};

export default TaskBoardPage;