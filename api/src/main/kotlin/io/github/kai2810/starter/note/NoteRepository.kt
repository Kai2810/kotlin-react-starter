package io.github.kai2810.starter.note

import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

data class Note(
	val id: UUID,
	val title: String,
	val body: String,
	val createdAt: Instant,
)

/** Plain SQL through JdbcClient: every query is visible and there is no ORM state to reason about. */
@Repository
class NoteRepository(private val jdbc: JdbcClient) {

	fun findPage(limit: Int, offset: Int): List<Note> =
		jdbc.sql("SELECT $COLUMNS FROM note ORDER BY created_at DESC, id DESC LIMIT :limit OFFSET :offset")
			.param("limit", limit)
			.param("offset", offset)
			.query(rowMapper)
			.list()

	fun findById(id: UUID): Note? =
		jdbc.sql("SELECT $COLUMNS FROM note WHERE id = :id")
			.param("id", id)
			.query(rowMapper)
			.optional()
			.orElse(null)

	fun insert(title: String, body: String): Note =
		jdbc.sql("INSERT INTO note (title, body) VALUES (:title, :body) RETURNING $COLUMNS")
			.param("title", title)
			.param("body", body)
			.query(rowMapper)
			.single()

	/** Returns false when no note had this id. */
	fun delete(id: UUID): Boolean =
		jdbc.sql("DELETE FROM note WHERE id = :id")
			.param("id", id)
			.update() == 1

	private companion object {
		const val COLUMNS = "id, title, body, created_at"

		val rowMapper = RowMapper { rs, _ ->
			Note(
				id = rs.getObject("id", UUID::class.java),
				title = rs.getString("title"),
				body = rs.getString("body"),
				createdAt = rs.getTimestamp("created_at").toInstant(),
			)
		}
	}
}
