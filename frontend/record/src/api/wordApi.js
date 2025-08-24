import api from './axios';

export const createWord = async (data) => api.post('/api/economy/word', data);
export const updateWord = async (data) => api.put('/api/economy/word', data);
export const fetchWords = async (params) => api.get('/api/economy/word/search', params);