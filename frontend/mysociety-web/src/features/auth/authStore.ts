import {create} from 'zustand'

export type AuthUser = {
    id: string
    email: string | null
    firstName: string
    lastName: string | null
    permissions: string[]
}

type AuthState = {
    accessToken: string | null
    refreshToken: string | null
    user: AuthUser | null
    authenticate: (accessToken: string, refreshToken: string, user: AuthUser) => void
    clear: () => void
}

export const useAuthStore = create<AuthState>((set) => ({
    accessToken: sessionStorage.getItem('accessToken'),
    refreshToken: sessionStorage.getItem('refreshToken'),
    user: null,
    authenticate: (accessToken, refreshToken, user) => {
        sessionStorage.setItem('accessToken', accessToken)
        sessionStorage.setItem('refreshToken', refreshToken)
        set({accessToken, refreshToken, user})
    },
    clear: () => {
        sessionStorage.removeItem('accessToken')
        sessionStorage.removeItem('refreshToken')
        set({accessToken: null, refreshToken: null, user: null})
    },
}))
