import api from './axios';

export const createActive = async (data) => api.post('/api/economy/active', data);
// export const updateEpic = async (data) => api.put('/api/economy/epic', data);
// export const deleteEpic = async (id) => api.delete(`/api/economy/epic/${id}`);
// export const createTask = async (data) => api.post('/api/economy/task', data);
// export const updateTask = async (data) => api.put('/api/economy/task', data);
// export const deleteTask = async (id) => api.delete(`/api/economy/task/${id}`);
// export const sortEpic = async (data) => api.post('api/economy/epic/sort', data);
// export const sortTask = async (data) => api.post('/api/economy/task/sort', data);
// export const fetchEpics = async () => api.get('/api/economy/epic');