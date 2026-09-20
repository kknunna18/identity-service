# MySociety Web

React and TypeScript administration client for the MySociety Identity Service.
It provides authenticated sign-in and a protected user-management view.

## Technology

React, TypeScript, Vite, React Router, Material UI, Axios, TanStack Query,
React Hook Form, Zod, Zustand, Vitest, React Testing Library, ESLint, and
Prettier.

## Run locally

```powershell
Copy-Item .env.example .env
npm install
npm run dev
```

The default API is `http://localhost:8081/api/v1`. Set
`VITE_API_BASE_URL=http://localhost:8080/api/v1` after adding an API Gateway.

## Quality checks

```powershell
npm run lint
npm run test
npm run build
```
