package io.github.kai2810.starter.note

import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.util.UUID

data class CreateNoteRequest(
	@field:NotBlank
	@field:Size(max = 200)
	val title: String,

	@field:Size(max = 10_000)
	val body: String = "",
)

class NoteNotFoundException(val id: UUID) : RuntimeException("Note $id does not exist")

@RestController
@RequestMapping("/api/notes")
class NoteController(private val notes: NoteRepository) {

	@GetMapping
	fun list(
		@RequestParam(defaultValue = "20") @Min(1) @Max(100) limit: Int,
		@RequestParam(defaultValue = "0") @Min(0) offset: Int,
	): List<Note> = notes.findPage(limit, offset)

	@GetMapping("/{id}")
	fun get(@PathVariable id: UUID): Note = notes.findById(id) ?: throw NoteNotFoundException(id)

	@PostMapping
	fun create(@Valid @RequestBody request: CreateNoteRequest): ResponseEntity<Note> {
		val note = notes.insert(request.title.trim(), request.body)
		return ResponseEntity.created(URI.create("/api/notes/${note.id}")).body(note)
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	fun delete(@PathVariable id: UUID) {
		if (!notes.delete(id)) throw NoteNotFoundException(id)
	}
}
