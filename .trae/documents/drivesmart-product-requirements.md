# DriveSmart App PRD (Product Requirements Document)

## 1. Purpose

* Build a professional, modern, mobile‑first web application for internal use by car rental company owners and workers.

* Provide fast access to fleet, bookings, customers, analytics and settings with consistent UI/UX and a unified design system.

## 2. Target Users

* Admin: full access to fleet, bookings, customers, analytics, audit logs, system settings.

* Worker: access to bookings, fleet operations, customer service; limited admin features.

## 3. Design System

* Color Palette (exact hex):

  * Light Lavender `#A7A5C6` (primary surfaces)

  * Slate Blue `#8797B2` (secondary/headers)

  * Steel Teal `#6D8A96` (accents/buttons)

  * Dark Slate `#5D707F` (text/dark elements)

  * Bright Cyan `#66CED6` (CTA/active states)

* Typography: Inter (body), Poppins (headings)

* Icons: inline SVG only; no external icon libraries

* Spacing, radius, shadows, transitions defined as CSS variables; touch‑friendly controls (≥44×44px)

* Dark Mode: theme variables in `themes.css` with a toggle; persist preference in localStorage

## 4. Responsive & Accessibility

* Breakpoints: Mobile <768px, Tablet 768–1024px, Desktop >1024px

* Mobile‑first layouts using CSS Grid/Flexbox; no horizontal scrolling

* ARIA labels for controls, keyboard navigation, screen reader support (focus states)

## 5. Navigation & App Shell

* Top bar: logo, quick actions, user menu (profile, settings, logout)

* Sidebar (mobile drawer): Dashboard, Fleet, Bookings, Customers, Reports (admin), Settings (admin)

* Bottom nav for mobile: Home, Fleet, Bookings, Customers, Profile

* Active state based on `pageClass` param; no request URI evaluation in templates

## 6. Core Pages & Features

* Dashboard

  * Stats cards (Total Vehicles, Available Cars, Active Bookings, Revenue)

  * Quick actions (Fleet Management, Booking Management, Customer Service, Analytics)

  * Recent activity list

* Fleet

  * List/grid view, search, filters

  * Add/Edit car forms (image upload, validation, availability toggle)

  * Car details (gallery, specs, availability, actions)

* Bookings

  * List/filter by status/date

  * New booking flow (pick car, dates, price preview)

  * Booking details/confirmation page (status changes: Confirm, Active, Completed, Cancel)

  * Admin quick‑create for a client (`/bookings/admin-new`)

* Customers

  * List/search, tags/segments, customer profile (history)

  * Import/Export CSV (deferred; non‑blocking link destinations)

* Admin

  * Dashboard overview (cards, charts)

  * Users management (roles, activation)

  * Reports (monthly/revenue/user activity widgets)

  * Settings (app config, sessions, email notifications)

  * Audit logs (read‑only table)

* Auth

  * Login (email+password), Register (Admin‑controlled), Logout

  * Redirect: `/dashboard` → `AuthController` routes admins to `/admin/dashboard`, others to `/`

## 7. UI Components

* Buttons: primary/secondary/accent/success; disabled states, loading spinners

* Cards: feature‑card, stat‑card, booking‑card; hover micro‑animations

* Forms: floating labels, inline validation messages, consistent inputs/selects/date pickers

* Tables: responsive, striped, sortable; minimal JS sorter and client‑side pagination

* Modals: centered with backdrop blur using palette colors

* Toasts: bottom‑right notifications (success, error, info) using palette accents

* Progress: multi‑step indicators (use all 5 colors)

## 8. Interaction Requirements

* Smooth scroll and reveal‑on‑scroll animations

* Real‑time validation in forms; submit disables while processing

* Lazy loading for images/content blocks

* Drag‑and‑drop (deferred; for reordering where applicable)

* Tooltips for help text (non‑blocking)

* Dynamic content refresh (e.g., dashboard stats every 5 minutes)

## 9. Browser Compatibility

* Chrome, Firefox, Safari, Edge (latest 2 versions)

* Progressive enhancement for older browsers; polyfills for critical ES features

## 10. Performance & SEO

* Semantic HTML5, meta tags, Open Graph

* Structured data (JSON‑LD) for business information (deferred file)

* Preload critical CSS; cache static assets via Spring resource handler

* Lazy image loading; optimized image sizes; minimal CSS/JS payload

## 11. Security & Access Control

* Spring Security roles: ADMIN, WORKER (default)

* Protected routes: `/cars/**`, `/bookings/**`, `/user/**`, `/admin/**` (role‑based)

* CSRF: disabled for server‑rendered forms in current scope; planned enable with token on future AJAX forms

* No external icon CDN; avoid third‑party scripts; do not log secrets

## 12. Data & Routes (UI Mapping)

* `/` → home dashboard (`home.html`)

* `/login` → `auth/login.html`

* `/register` → `auth/register.html`

* `/cars` → `cars/list.html`; `/cars/add`, `/cars/edit`, `/cars/details?id`

* `/cars/search` → `cars/search.html`

* `/bookings` → `bookings/list.html`; `/bookings/new` (user); `/bookings/{id}` (details)

* `/bookings/admin-new` → `bookings/admin-new.html` (admin)

* `/bookings/admin/create` (POST) → redirects to `/bookings/{id}`

* `/customers` → `customers/list.html`

* `/user/profile` → `user/profile.html`

* `/admin/dashboard` → `admin/dashboard.html`

* `/admin/users`, `/admin/reports`, `/admin/settings`, `/admin/audit-logs`

## 13. Acceptance Criteria

* All pages load with the unified design system and exact palette.

* No external CSS/JS icon libraries; all icons are inline SVG.

* Navigation and bottom nav work on mobile; active state via `pageClass`.

* Car list “View” correctly routes to `CarController` details; booking actions POST to controller endpoints.

* Forms have validation and error feedback; buttons are touch‑friendly.

* Pages are accessible (focus states, ARIA, keyboard navigation) and responsive without horizontal scrolling.

## 14. Deliverables

* Complete HTML/CSS/JS implementation for pages and components.

* Mobile, tablet, desktop layouts.

* Connected actions and routes for fleet, bookings, customers and admin.

* Accessibility and performance standards met.

## 15. Out of Scope (Phase 1)

* Payments processing, invoices PDF generation.

* Real push notifications.

* Full offline mode beyond basic cache of CSS/JS.

## 16. Metrics

* Median first contentful paint on mobile < 2.5s.

* Error rate in booking operations < 1%.

* Page responsiveness: no horizontal scroll at mobile widths.

