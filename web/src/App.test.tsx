import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { afterEach, describe, expect, it, vi } from 'vitest'
import App from './App'

function jsonResponse(status: number, body: unknown) {
  return new Response(JSON.stringify(body), {
    status,
    headers: { 'Content-Type': 'application/json' },
  })
}

afterEach(() => {
  vi.unstubAllGlobals()
})

describe('App', () => {
  it('shows the empty state when there are no notes', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(jsonResponse(200, [])))

    render(<App />)

    expect(await screen.findByText(/no notes yet/i)).toBeInTheDocument()
  })

  it('shows the error and recovers on retry', async () => {
    const fetchMock = vi
      .fn()
      .mockResolvedValueOnce(
        jsonResponse(503, {
          title: 'Service Unavailable',
          detail: 'Database is down',
        }),
      )
      .mockResolvedValueOnce(jsonResponse(200, []))
    vi.stubGlobal('fetch', fetchMock)

    render(<App />)
    expect(await screen.findByText(/database is down/i)).toBeInTheDocument()

    await userEvent.click(screen.getByRole('button', { name: /try again/i }))

    expect(await screen.findByText(/no notes yet/i)).toBeInTheDocument()
  })

  it('adds a created note to the top of the list', async () => {
    const created = {
      id: '1',
      title: 'Pack adapters',
      body: '',
      createdAt: '2026-11-10T08:00:00Z',
    }
    const fetchMock = vi
      .fn()
      .mockResolvedValueOnce(jsonResponse(200, []))
      .mockResolvedValueOnce(jsonResponse(201, created))
    vi.stubGlobal('fetch', fetchMock)

    render(<App />)
    await screen.findByText(/no notes yet/i)
    await userEvent.type(screen.getByLabelText('Title'), 'Pack adapters')
    await userEvent.click(screen.getByRole('button', { name: /add note/i }))

    expect(await screen.findByText('Pack adapters')).toBeInTheDocument()
    expect(fetchMock).toHaveBeenLastCalledWith(
      '/api/notes',
      expect.objectContaining({
        method: 'POST',
        body: JSON.stringify({ title: 'Pack adapters', body: '' }),
      }),
    )
  })
})
