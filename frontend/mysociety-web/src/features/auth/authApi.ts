import {axiosClient} from '../../api/axiosClient'
import type {AuthUser} from './authStore'

export type LoginInput = { identifier: string; password: string }
type TokenResponse = { accessToken: string; refreshToken: string; tokenType: string; expiresAt: string }

export async function login(input: LoginInput): Promise<TokenResponse> {
    const {data} = await axiosClient.post<TokenResponse>('/auth/login', input)
    return data
}

export async function getCurrentUser(accessToken: string): Promise<AuthUser> {
    const {data} = await axiosClient.get<AuthUser>('/auth/me', {
        headers: {Authorization: `Bearer ${accessToken}`},
    })
    return data
}

export async function logout(refreshToken: string): Promise<void> {
    await axiosClient.post('/auth/logout', {refreshToken})
}
