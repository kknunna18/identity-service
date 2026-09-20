import {
    Alert,
    CircularProgress,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Typography
} from '@mui/material'
import {useQuery} from '@tanstack/react-query'
import {axiosClient} from '../../api/axiosClient'

type UserSummary = { id: string; email: string | null; firstName: string; lastName: string | null; status: string }
type PageResponse = { content: UserSummary[] }

async function getUsers(): Promise<PageResponse> {
    const {data} = await axiosClient.get<PageResponse>('/users')
    return data
}

export function UsersPage() {
    const query = useQuery({queryKey: ['users'], queryFn: getUsers})

    return (
        <>
            <Typography component="h1" variant="h4" gutterBottom>User management</Typography>
            {query.isLoading && <CircularProgress aria-label="Loading users"/>}
            {query.isError &&
                <Alert severity="error">Users could not be loaded. Verify your permission and API connection.</Alert>}
            {query.data && (
                <TableContainer component={Paper}>
                    <Table>
                        <TableHead><TableRow><TableCell>Name</TableCell><TableCell>Email</TableCell><TableCell>Status</TableCell></TableRow></TableHead>
                        <TableBody>
                            {query.data.content.map((user) => <TableRow
                                key={user.id}><TableCell>{`${user.firstName} ${user.lastName ?? ''}`.trim()}</TableCell><TableCell>{user.email ?? '—'}</TableCell><TableCell>{user.status}</TableCell></TableRow>)}
                        </TableBody>
                    </Table>
                </TableContainer>
            )}
        </>
    )
}
