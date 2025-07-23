import axios from 'axios';

const API_URL = '/api/tasks';

// Create axios instance with authorization header
const api = axios.create();

// Add request interceptor to add authorization header
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export interface Task {
  id?: number;
  title: string;
  description: string;
  status: 'TODO' | 'IN_PROGRESS' | 'DONE';
  ownerId?: number;
  ownerName?: string;
  createdAt?: string;
  updatedAt?: string;
}

const getAllTasks = async (): Promise<Task[]> => {
  const response = await api.get(API_URL);
  return response.data;
};

const getMyTasks = async (): Promise<Task[]> => {
  const response = await api.get(`${API_URL}/my-tasks`);
  return response.data;
};

const getTasksByStatus = async (status: string): Promise<Task[]> => {
  const response = await api.get(`${API_URL}/my-tasks/status/${status}`);
  return response.data;
};

const getTaskById = async (id: number): Promise<Task> => {
  const response = await api.get(`${API_URL}/${id}`);
  return response.data;
};

const createTask = async (task: Task): Promise<Task> => {
  const response = await api.post(API_URL, task);
  return response.data;
};

const updateTask = async (id: number, task: Task): Promise<Task> => {
  const response = await api.put(`${API_URL}/${id}`, task);
  return response.data;
};

const deleteTask = async (id: number): Promise<void> => {
  await api.delete(`${API_URL}/${id}`);
};

const taskService = {
  getAllTasks,
  getMyTasks,
  getTasksByStatus,
  getTaskById,
  createTask,
  updateTask,
  deleteTask
};

export default taskService;