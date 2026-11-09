package io.github.kai2810.starter

import io.github.kai2810.starter.note.NoteNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Maps domain exceptions to RFC 9457 problem details. Framework errors (validation,
 * unreadable JSON) already get problem details from spring.mvc.problemdetails.enabled.
 */
@RestControllerAdvice
class ApiExceptionHandler {

	@ExceptionHandler
	fun noteNotFound(e: NoteNotFoundException): ProblemDetail =
		ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message ?: "Not found").apply {
			title = "Note not found"
		}
}
