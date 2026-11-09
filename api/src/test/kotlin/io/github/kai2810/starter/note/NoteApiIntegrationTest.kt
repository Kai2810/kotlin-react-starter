package io.github.kai2810.starter.note

import io.github.kai2810.starter.TestcontainersConfiguration
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.core.env.Environment
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/** The full stack against a real Postgres in Testcontainers, including the Flyway migration. */
@Import(TestcontainersConfiguration::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NoteApiIntegrationTest {

	@Autowired
	lateinit var env: Environment

	private val client by lazy {
		RestClient.create("http://localhost:${env.getRequiredProperty("local.server.port")}")
	}

	@Test
	fun `create, list, delete, then the note is gone`() {
		val created = client.post().uri("/api/notes")
			.contentType(MediaType.APPLICATION_JSON)
			.body(mapOf("title" to "  Buy adapters  ", "body" to "USB-C to HDMI"))
			.retrieve()
			.toEntity(Note::class.java)
		assertEquals(HttpStatus.CREATED, created.statusCode)
		val note = assertNotNull(created.body)
		assertEquals("Buy adapters", note.title)
		assertEquals("/api/notes/${note.id}", created.headers.location?.toString())

		val page = client.get().uri("/api/notes?limit=10").retrieve().body<List<Note>>()
		assertEquals(note.id, page?.first()?.id)

		val deleted = client.delete().uri("/api/notes/${note.id}").retrieve().toBodilessEntity()
		assertEquals(HttpStatus.NO_CONTENT, deleted.statusCode)

		val status = client.get().uri("/api/notes/${note.id}").exchange { _, response -> response.statusCode }
		assertEquals(HttpStatus.NOT_FOUND, status)
	}
}
