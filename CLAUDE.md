# Kotlin + React starter

A full-stack starter: Kotlin + Spring Boot API in `api/`, React + Vite + TypeScript in `web/`, Postgres.

## Commands

- `make dev`: API on :8080 (starts Postgres through Docker Compose) and web on :5173
- `make test`: API tests (unit + Testcontainers) and web tests (Vitest)
- `make lint`: oxlint and Prettier for the web app
- One API test class: `cd api && ./gradlew test --tests '*NoteControllerTest'`
- One web test file: `cd web && npx vitest run src/App.test.tsx`

## Layout

- `api/src/main/kotlin/.../note/`: one feature per package (controller, request types, repository)
- `api/src/main/resources/db/migration/`: Flyway migrations, `V<n>__<what>.sql`, never edited once merged
- `web/src/api.ts`: the only place that calls `fetch`
- `web/src/App.tsx`: UI, with explicit loading, empty and error states

## Conventions

- SQL through `JdbcClient`, written out in the repository. No JPA.
- Errors are RFC 9457 problem details: domain exceptions map in `ApiExceptionHandler`.
- Validate at the edge with Bean Validation (`@field:` targets on Kotlin properties).
- The web app calls `/api/...` relative paths; Vite proxies them to :8080.
- Prettier style for web code: no semicolons, single quotes.

## How to work here

- Propose a plan before multi-file changes, and keep each diff small enough to review.
- Every behaviour change comes with a test. Run `make test` before saying something works.
- Ask before adding a dependency, changing the schema, or touching CI.
- Never read or write `.env` files or put secrets in code.
