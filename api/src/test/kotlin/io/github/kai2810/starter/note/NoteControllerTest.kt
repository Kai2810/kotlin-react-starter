package io.github.kai2810.starter.note

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.util.UUID

/** Web layer only: routing, validation and error mapping, with the repository mocked. */
@WebMvcTest(NoteController::class)
class NoteControllerTest {

	@Autowired
	lateinit var mockMvc: MockMvc

	@MockitoBean
	lateinit var notes: NoteRepository

	@Test
	fun `unknown id returns a 404 problem detail`() {
		mockMvc.get("/api/notes/${UUID.randomUUID()}").andExpect {
			status { isNotFound() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.title") { value("Note not found") }
		}
	}

	@Test
	fun `blank title is rejected before it reaches the database`() {
		mockMvc.post("/api/notes") {
			contentType = MediaType.APPLICATION_JSON
			content = """{"title": "  ", "body": "x"}"""
		}.andExpect {
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
		}
	}

	@Test
	fun `page size above the limit is rejected`() {
		mockMvc.get("/api/notes?limit=500").andExpect {
			status { isBadRequest() }
		}
	}
}
