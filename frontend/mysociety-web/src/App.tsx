import {BrowserRouter, Navigate, Route, Routes} from 'react-router-dom'
import {AuthenticatedLayout} from './features/auth/AuthenticatedLayout'
import {RequireAuth} from './features/auth/RequireAuth'
import {LoginPage} from './features/auth/LoginPage'
import {UsersPage} from './features/users/UsersPage'

export default function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/login" element={<LoginPage/>}/>
                <Route element={<RequireAuth/>}>
                    <Route element={<AuthenticatedLayout/>}>
                        <Route path="/users" element={<UsersPage/>}/>
                    </Route>
                </Route>
                <Route path="*" element={<Navigate to="/users" replace/>}/>
            </Routes>
        </BrowserRouter>
    )
}
