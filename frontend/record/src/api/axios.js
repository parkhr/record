// src/api/axios.js
import axios from 'axios'

const api = axios.create({
//   baseURL: import.meta.env.VITE_API_BASE_URL || '/', // Vite 기준
  baseURL: 'http://localhost:8081',
  timeout: 10000,
})

export default api