import api from './axios';

export const updateReport = async (data) => api.put('/api/economy/report/task', data);
export const createReport = async (data) => api.post('/api/economy/report/task', data);
export const fetchReport = async () => api.get('/api/economy/report');
export const deleteReport = async (id) => api.delete(`/api/economy/report/task/${id}`);
export const fetchStatisticReport = async (reportId) => api.get(`/api/economy/report/task/statistic/${reportId}`);
// export const game = async () => api.get('/api/economy/word/game');
// export const fetchAttempts = async () => api.get('/api/economy/word/attempts');
// export const fetchWordStatus = async () => api.get('/api/economy/word/status');