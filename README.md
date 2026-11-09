# Kotlin + React starter

[![CI](https://github.com/Kai2810/kotlin-react-starter/actions/workflows/ci.yml/badge.svg)](https://github.com/Kai2810/kotlin-react-starter/actions/workflows/ci.yml)

A small full-stack starter I use to build and demo features quickly: a Kotlin + Spring Boot API, a React + TypeScript front end and Postgres, with tests and CI from the first commit. The example feature is a deliberately simple notes resource that shows the conventions end to end.

## Run it

You need JDK 21, Node 24 and Docker.

```bash
make dev
```

That starts Postgres (through Spring Boot's Docker Compose support), the API on http://localhost:8080 and the web app on http://localhost:5173. The API docs are at http://localhost:8080/swagger-ui.html.

```bash
make test
```

That runs the API's unit and Testcontainers tests, then the web app's Vitest suite.

## How it fits together

```
browser ──► Vite dev server :5173 ──/api──► Spring Boot :8080 ──► Postgres
            (React + TS)          proxy     (Kotlin, JdbcClient)   (Flyway migrations)
```

| Path | What's there |
|---|---|
| `api/` | Spring Boot 4 and Kotlin. One package per feature, SQL through `JdbcClient`, Flyway migrations, RFC 9457 problem details for errors |
| `web/` | React 19, Vite and TypeScript. One API client module, explicit loading, empty and error states, Vitest and Testing Library |
| `compose.yaml` | Postgres for local development |
| `.github/workflows/ci.yml` | Tests, lint and build for both apps on every push and pull request |
| `CLAUDE.md`, `.claude/`, `docs/ai-workflow.md` | How AI-assisted work is done here, and the guardrails |

## Decisions

| Decision | Why | Rejected |
|---|---|---|
| `JdbcClient` with hand-written SQL | Every query is visible and reviewable, and there's no ORM session state to reason about | JPA/Hibernate: more magic than a small service needs |
| Problem details (RFC 9457) for every error | One error shape for clients and tests, and Spring supports it natively | Ad-hoc error JSON per endpoint |
| Testcontainers for integration tests | Tests run against the same Postgres and migrations as production | H2 in Postgres mode: subtly different SQL, false confidence |
| Vite proxy for `/api` | The browser stays on one origin, so no CORS configuration | CORS rules on the API |
| One repo for both apps | One PR can change the API and the UI together, and CI checks both | Separate repos per app |

## Next steps I'd take

- Cursor-based pagination once lists grow (offset is fine at this size)
- Authentication in front of the API
- A container image and deployment pipeline
