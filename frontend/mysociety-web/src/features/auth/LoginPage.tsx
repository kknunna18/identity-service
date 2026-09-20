import {zodResolver} from '@hookform/resolvers/zod'
import {Alert, Box, Button, Paper, TextField, Typography} from '@mui/material'
import {useMutation} from '@tanstack/react-query'
import {useForm} from 'react-hook-form'
import {useNavigate} from 'react-router-dom'
import {z} from 'zod'
import {getCurrentUser, login} from './authApi'
import {useAuthStore} from './authStore'

const loginSchema = z.object({
    identifier: z.string().trim().min(1, 'Email or mobile number is required'),
    password: z.string().min(1, 'Password is required'),
})
type LoginForm = z.infer<typeof loginSchema>

export function LoginPage() {
    const navigate = useNavigate()
    const authenticate = useAuthStore((state) => state.authenticate)
    const form = useForm<LoginForm>({resolver: zodResolver(loginSchema)})
    const mutation = useMutation({
        mutationFn: login,
        onSuccess: async (result) => {
            const user = await getCurrentUser(result.accessToken)
            authenticate(result.accessToken, result.refreshToken, user)
            navigate('/users')
        },
    })

    return (
        <Box sx={{display: 'grid', minHeight: '100vh', placeItems: 'center', p: 2}}>
            <Paper component="form" onSubmit={form.handleSubmit((values) => mutation.mutate(values))}
                   sx={{width: '100%', maxWidth: 420, p: 4}}>
                <Typography component="h1" variant="h4" gutterBottom>Sign in</Typography>
                <Typography color="text.secondary" sx={{mb: 3}}>Manage MySociety users and access.</Typography>
                {mutation.isError &&
                    <Alert severity="error" sx={{mb: 2}}>Sign-in failed. Check your credentials and account
                        status.</Alert>}
                <TextField label="Email or mobile number" autoComplete="username" fullWidth margin="normal"
                           error={Boolean(form.formState.errors.identifier)}
                           helperText={form.formState.errors.identifier?.message} {...form.register('identifier')} />
                <TextField label="Password" type="password" autoComplete="current-password" fullWidth margin="normal"
                           error={Boolean(form.formState.errors.password)}
                           helperText={form.formState.errors.password?.message} {...form.register('password')} />
                <Button type="submit" variant="contained" fullWidth sx={{mt: 3}} disabled={mutation.isPending}>Sign
                    in</Button>
            </Paper>
        </Box>
    )
}
