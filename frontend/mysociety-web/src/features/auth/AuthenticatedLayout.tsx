import LogoutIcon from '@mui/icons-material/Logout'
import PeopleIcon from '@mui/icons-material/People'
import {Alert, AppBar, Box, Button, Container, Toolbar, Typography} from '@mui/material'
import {useState} from 'react'
import {Outlet, useNavigate} from 'react-router-dom'
import {logout} from './authApi'
import {useAuthStore} from './authStore'

export function AuthenticatedLayout() {
    const navigate = useNavigate()
    const {clear, refreshToken} = useAuthStore()
    const [logoutError, setLogoutError] = useState(false)

    const handleLogout = async () => {
        if (!refreshToken) {
            clear()
            navigate('/login')
            return
        }

        try {
            await logout(refreshToken)
        } catch {
            setLogoutError(true)
            return
        }

        clear()
        navigate('/login')
    }

    return (
        <>
            <AppBar position="static">
                <Toolbar>
                    <PeopleIcon sx={{mr: 1}}/>
                    <Typography variant="h6" sx={{flexGrow: 1}}>MySociety Identity</Typography>
                    <Button color="inherit" startIcon={<LogoutIcon/>} onClick={handleLogout}>Sign out</Button>
                </Toolbar>
            </AppBar>
            <Container component="main" maxWidth="lg">
                <Box sx={{py: 4}}>
                    {logoutError &&
                        <Alert severity="error" sx={{mb: 2}}>Sign-out could not be completed. Please retry.</Alert>}
                    <Outlet/>
                </Box>
            </Container>
        </>
    )
}
