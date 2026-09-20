import axios from 'axios'
import {useAuthStore} from '../features/auth/authStore'

export const axiosClient = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8081/api/v1',
    headers: {'Content-Type': 'application/json'},
})

axiosClient.interceptors.request.use((config) => {
    const token = useAuthStore.getState().accessToken
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }
    return config
})
