# MySociety UI Requirements

This document consolidates the frontend requirements from the
[shared architecture conversation](https://chatgpt.com/share/6aaf83aa-96a8-83ee-8390-887b2f9243bd).
It defines the target user experience for the MySociety web application. Domain
screens consume the API Gateway at `/api/v1`; they must not access service
databases directly.

## Technology and application architecture

| Area | Requirement |
|---|---|
| Language/build | React, TypeScript, Vite |
| Navigation | React Router with guarded routes |
| UI system | Material UI with a shared light theme; dark theme when practical |
| Server state | TanStack Query |
| Forms | React Hook Form with Zod validation |
| HTTP | Axios through a shared API client; no HTTP calls directly from page components |
| Client state | Zustand for small global/session state |
| Reporting visuals | Recharts |
| Dates | date-fns |
| Quality | Vitest, React Testing Library, ESLint, Prettier |

Use strict TypeScript—no `any` or unnecessary assertions—and reusable feature
components. API code must remain replaceable: page components call typed service
modules, not hard-coded endpoints.

## Roles and authorization

The UI supports **Resident**, **Society Administrator**, **Committee Member**,
**Accountant**, **Security Guard**, **Facility Manager**, **Vendor/Technician**,
and **Platform Administrator** roles.

Navigation items, route access, visible actions, dashboard data, and bulk
operations must be derived from the signed-in user's permitted role and
permissions. A user must not gain capability merely because a menu item is
hidden; the API remains authoritative.

## Visual system and application shell

- Use an apartment/community-management SaaS design.
- Theme colors: primary `#1F4E78`, secondary `#2E74B5`, success `#2E7D32`,
  warning `#ED6C02`, error `#D32F2F`, and background `#F5F7FA`.
- Use white cards with subtle borders/shadows, 8–12px rounded corners,
  consistent spacing, consistent icons, accessible contrast, and useful empty
  states.
- Avoid excessive motion, gradients, glassmorphism, stock photography, giant
  illustrations, watermarking, browser chrome, raw technical errors, and blank
  dashboard space.
- Provide a collapsible desktop sidebar, top navigation, page title and
  breadcrumb, society selector, notification icon, avatar/profile menu, and
  main content area. On mobile, the sidebar becomes a navigation drawer.
- Do not introduce a role selector to the login experience.

## Responsive behavior

| Breakpoint | Requirements |
|---|---|
| Mobile: 320–767px | Mobile drawer; single-column forms; full-screen dialogs where needed; login illustration hidden |
| Tablet: 768–1023px | Fluid cards and responsive forms/tables |
| Desktop: 1024px+ | Desktop-first administration layouts and persistent/collapsible sidebar |

Tables must scroll horizontally when necessary, with card alternatives for
important mobile workflows. Dashboard cards wrap responsively.

## Authentication and session pages

Required routes/pages:

1. Login
2. Forgot password
3. Reset password
4. OTP/MFA verification
5. Accept society invitation
6. Select society
7. Unauthorized access
8. Session expired

### Login screen

The desktop layout is two columns:

- **Left:** MySociety logo/application name; **“Your community, connected.”**;
  the description **“Manage payments, complaints, visitors and community
  updates in one secure place.”**; and an apartment/community illustration.
- **Right:** white login card; **“Welcome back”**; **“Sign in to manage your
  society.”**; identifier/password form; remember-email checkbox; forgot
  password link; full-width sign-in button; help and privacy links.

The form requires an email/mobile identifier and password, supports password
visibility, uses `autocomplete="username"` and
`autocomplete="current-password"`, allows paste and password-manager autofill,
disables submission while loading, and shows **“Signing in...”**. It displays
field-level validation, invalid-credential, account-locked, and server-error
states without revealing whether an account exists. The identifier is retained
after failure.

Remembered email may be saved to `localStorage`; access and refresh tokens must
never be stored there. Use the existing Axios/authentication contract, read
`VITE_API_BASE_URL`, maintain access tokens in memory, use an HttpOnly
refresh-token cookie, and send credentials on authentication requests. After
login: one active society goes to its dashboard; multiple societies go to
`/select-society`; MFA-required users go to `/verify-mfa`.

When refreshing an existing login UI, preserve existing request/response
contracts, authentication handlers, Axios interceptors, Zustand state, routing,
refresh-token handling, validation, loading/error behavior, and backend code.
Change only the presentation structure and styles.

## Dashboards

Route: `/dashboard`. Content is role specific.

| Audience | Required information |
|---|---|
| Administrator | Total/occupied units, residents, outstanding maintenance, collection %, open complaints, visitors inside, and today's bookings |
| Resident | Outstanding amount, next payment due date, open complaints, upcoming visitors/bookings, and latest notices |
| Security Guard | Expected visitors, visitors currently inside, completed check-outs, rejected/expired approvals, and quick visitor search |

Administrator analytics include monthly billed vs. collected, payment-method
distribution, complaint-status and category distributions, and monthly visitor
trend. Supporting sections include recent payments, recently created
complaints, upcoming bookings, notices, overdue invoices, and quick actions.

## Functional modules and routes

| Module | Primary routes | UI requirements |
|---|---|---|
| Society administration | `/societies`, `/societies/:societyId`, `/buildings`, `/units` | Society profile; building/wing and unit administration; add/edit/view, search/filter/sort/page, CSV import/export, bulk actions, confirmations, validation and empty states |
| Residents and households | `/residents`, `/residents/new`, `/residents/:residentId`, `/residents/:residentId/edit` | Profile/contact details, household/unit relationship, ownership/tenant status, move-in/out, vehicles, and history |
| Finance | `/charge-heads`, `/billing-runs`, `/invoices`, `/payments`, `/receipts` | Charge-head setup, billing progress, invoice list/detail/adjustment, payment state, receipts, reconciliation and refunds; financial commands expose clear idempotency/loading/failure states |
| Operations | `/complaints`, `/work-orders` | Complaint list/detail, category/status/priority, comments, SLA/escalation indicators, assignment, work-order workflow, resolution and feedback |
| Community | `/visitors`, `/facilities`, `/bookings` | Visitor approval/check-in/out, security search, facility availability, booking calendar/list, conflicts, cancellation, and clear status chips |
| Communication | `/notices`, `/documents`, `/notifications` | Notice authoring/audiences, document/attachment management, notification templates, delivery/status views, and readable empty/error states |
| Reporting and audit | `/reports`, `/exports`, `/audit-events` | Role-appropriate dashboards, filterable reports, export request/status/download metadata, and read-only searchable audit trails |
| Account/settings | `/profile`, `/settings` | Profile, preference, society settings, and notification preferences within role permissions |

Society forms cover name, registration number, address, contacts, timezone,
currency, logo, and active status. Building forms cover name/code/floors/unit
count/status. Unit forms cover number/building/floor/type/area/occupancy/
ownership/owner/tenant/parking/status.

## Security, accessibility, and quality

- Use visible labels, keyboard navigation, visible focus indicators,
  `aria-describedby` for errors, accessible labels, and accessible color
  contrast.
- Use confirmation dialogs for destructive/bulk actions and never show raw
  server stack traces or security details.
- Send society context only through the authenticated/session contract; never
  rely on a manually entered society ID for authorization.
- Render API validation and RFC 9457 errors in user-friendly messages.
- Test required-field validation, password visibility, success/failure and
  loading authentication states, guarded routes, role-based menus/actions,
  responsive rendering, empty/error states, and critical module workflows.

## Demo-mode requirement

If the frontend runs before live APIs are available, provide a mock service
layer rather than embedding mock data in components. Demo accounts are required
for each supported role; the original prototype specifies `Password@123` as
the mock-only password. Clearly mark mock mode and never ship those accounts or
credentials to production.
