CREATE TABLE note (
    id         uuid        PRIMARY KEY DEFAULT gen_random_uuid(),
    title      text        NOT NULL CHECK (length(title) BETWEEN 1 AND 200),
    body       text        NOT NULL DEFAULT '',
    created_at timestamptz NOT NULL DEFAULT now()
);

-- Serves the newest-first list; id breaks ties so pages are stable.
CREATE INDEX note_created_at_idx ON note (created_at DESC, id DESC);
