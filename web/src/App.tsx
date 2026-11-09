import { useEffect, useState, type FormEvent } from 'react'
import { api, type Note } from './api'

type NotesState =
  | { status: 'loading' }
  | { status: 'error'; message: string }
  | { status: 'ready'; notes: Note[] }

function errorMessage(error: unknown): string {
  return error instanceof Error ? error.message : 'Something went wrong'
}

export default function App() {
  const [notesState, setNotesState] = useState<NotesState>({
    status: 'loading',
  })
  const [reloadKey, setReloadKey] = useState(0)
  const [title, setTitle] = useState('')
  const [body, setBody] = useState('')
  const [saving, setSaving] = useState(false)
  const [actionError, setActionError] = useState<string | null>(null)

  useEffect(() => {
    let ignore = false
    api.listNotes().then(
      (notes) => !ignore && setNotesState({ status: 'ready', notes }),
      (error) =>
        !ignore &&
        setNotesState({ status: 'error', message: errorMessage(error) }),
    )
    return () => {
      ignore = true
    }
  }, [reloadKey])

  function retry() {
    setNotesState({ status: 'loading' })
    setReloadKey((key) => key + 1)
  }

  function updateNotes(change: (notes: Note[]) => Note[]) {
    setNotesState((current) =>
      current.status === 'ready'
        ? { status: 'ready', notes: change(current.notes) }
        : current,
    )
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setSaving(true)
    setActionError(null)
    try {
      const note = await api.createNote(title, body)
      updateNotes((notes) => [note, ...notes])
      setTitle('')
      setBody('')
    } catch (error) {
      setActionError(errorMessage(error))
    } finally {
      setSaving(false)
    }
  }

  async function handleDelete(id: string) {
    setActionError(null)
    try {
      await api.deleteNote(id)
      updateNotes((notes) => notes.filter((note) => note.id !== id))
    } catch (error) {
      setActionError(errorMessage(error))
    }
  }

  return (
    <main>
      <h1>Notes</h1>
      <p className="muted">
        A starter app: Kotlin and Spring Boot API, React front end, Postgres.
      </p>

      <form onSubmit={handleSubmit}>
        <label htmlFor="title">Title</label>
        <input
          id="title"
          value={title}
          maxLength={200}
          required
          onChange={(event) => setTitle(event.target.value)}
        />
        <label htmlFor="body">Body</label>
        <textarea
          id="body"
          value={body}
          rows={3}
          onChange={(event) => setBody(event.target.value)}
        />
        <button type="submit" disabled={saving || title.trim() === ''}>
          {saving ? 'Adding…' : 'Add note'}
        </button>
        {actionError && <p role="alert">{actionError}</p>}
      </form>

      <section aria-busy={notesState.status === 'loading'}>
        {notesState.status === 'loading' && <p>Loading notes…</p>}
        {notesState.status === 'error' && (
          <div role="alert">
            <p>Couldn't load notes: {notesState.message}</p>
            <button onClick={retry}>Try again</button>
          </div>
        )}
        {notesState.status === 'ready' && notesState.notes.length === 0 && (
          <p className="muted">No notes yet. Add the first one above.</p>
        )}
        {notesState.status === 'ready' && notesState.notes.length > 0 && (
          <ul>
            {notesState.notes.map((note) => (
              <li key={note.id}>
                <div>
                  <strong>{note.title}</strong>
                  {note.body && <p>{note.body}</p>}
                </div>
                <button
                  aria-label={`Delete ${note.title}`}
                  onClick={() => handleDelete(note.id)}
                >
                  Delete
                </button>
              </li>
            ))}
          </ul>
        )}
      </section>
    </main>
  )
}
