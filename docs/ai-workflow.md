# How I work with Claude Code in this repo

The AI writes most of the code. I own the problem, the design and every line that gets merged.

## The loop

1. **Frame it myself.** Say what we're building, the smallest version worth shipping, and what's out of scope.
2. **Plan first.** For anything touching more than one file, ask for a plan (plan mode), correct it, then let it edit.
3. **Small diffs.** One behaviour per step: an endpoint, a migration, a component. Big diffs don't get reviewed properly.
4. **Read every diff.** I check names, error handling, edge cases and anything I didn't ask for.
5. **Prove it.** A test for the new behaviour, `make test`, then try it in the running app.
6. **Commit.** Small commits with messages that say why, so any step can be undone.

## What I delegate, and what I don't

| Delegate | Keep |
|---|---|
| Boilerplate, wiring, test scaffolding | The data model and API shape |
| Writing the first version of a test from my cases | Choosing which cases matter |
| Mechanical refactors I've described | Decisions about consistency, idempotency and failure modes |
| Explaining unfamiliar library APIs | Security-sensitive code, reviewed line by line |

## When it goes wrong

- Two failed corrections in a row: stop, `/clear` or `/rewind`, and restate the task with what was learned.
- If a one-line fix is faster to type than to describe, I type it.
- Guardrails live in `.claude/settings.json` (no `.env` reads, ask before `git push`), not in hope.
