import {Navigate, Outlet, useLocation} from 'react-router-dom'
import {useAuthStore} from './authStore'

export function RequireAuth() {
    const accessToken = useAuthStore((state) => state.accessToken)
    const location = useLocation()
    return accessToken ? <Outlet/> : <Navigate to="/login" replace state={{from: location}}/>
}
