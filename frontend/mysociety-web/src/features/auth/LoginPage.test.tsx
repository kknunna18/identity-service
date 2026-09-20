import {QueryClient, QueryClientProvider} from '@tanstack/react-query'
import {render, screen} from '@testing-library/react'
import {BrowserRouter} from 'react-router-dom'
import {describe, expect, it} from 'vitest'
import {LoginPage} from './LoginPage'

describe('LoginPage', () => {
    it('renders sign-in controls', () => {
        render(
            <QueryClientProvider client={new QueryClient()}>
                <BrowserRouter><LoginPage/></BrowserRouter>
            </QueryClientProvider>,
        )

        expect(screen.getByRole('heading', {name: 'Sign in'})).toBeInTheDocument()
        expect(screen.getByRole('button', {name: 'Sign in'})).toBeInTheDocument()
    })
})
