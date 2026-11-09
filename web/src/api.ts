export type Note = {
  id: string
  title: string
  body: string
  createdAt: string
}

export class ApiError extends Error {
  readonly status: number

  constructor(status: number, message: string) {
    super(message)
    this.status = status
  }
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(path, {
    ...init,
    headers: { 'Content-Type': 'application/json', ...init?.headers },
  })
  if (!response.ok) {
    // The API answers errors with RFC 9457 problem details. Fall back to the status text.
    const problem = (await response.json().catch(() => null)) as {
      detail?: string
      title?: string
    } | null
    throw new ApiError(
      response.status,
      problem?.detail ?? problem?.title ?? response.statusText,
    )
  }
  return (response.status === 204 ? undefined : await response.json()) as T
}

export const api = {
  listNotes: () => request<Note[]>('/api/notes?limit=50'),
  createNote: (title: string, body: string) =>
    request<Note>('/api/notes', {
      method: 'POST',
      body: JSON.stringify({ title, body }),
    }),
  deleteNote: (id: string) =>
    request<void>(`/api/notes/${id}`, { method: 'DELETE' }),
}
