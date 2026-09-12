package it.uniroma3.FestivalCinema.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Sezione 10-11: gli endpoint REST devono restituire opportuni codici di stato
// HTTP e gestire correttamente le condizioni di errore.
@RestControllerAdvice(annotations = org.springframework.web.bind.annotation.RestController.class)
public class ApiExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Object> gestisciNonTrovato(IllegalArgumentException ex) {
		return corpo(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<Object> gestisciConflitto(IllegalStateException ex) {
		return corpo(HttpStatus.CONFLICT, ex.getMessage());
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<Object> gestisciAccessoNegato(AccessDeniedException ex) {
		return corpo(HttpStatus.FORBIDDEN, ex.getMessage());
	}

	@ExceptionHandler(UsernameGiaRegistratoException.class)
	public ResponseEntity<Object> gestisciUsernameDuplicato(UsernameGiaRegistratoException ex) {
		return corpo(HttpStatus.CONFLICT, ex.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> gestisciValidazione(MethodArgumentNotValidException ex) {
		String messaggio = ex.getBindingResult().getFieldErrors().stream()
			.map(err -> err.getField() + ": " + err.getDefaultMessage())
			.reduce((a, b) -> a + "; " + b)
			.orElse("Dati non validi");
		return corpo(HttpStatus.BAD_REQUEST, messaggio);
	}

	private ResponseEntity<Object> corpo(HttpStatus status, String messaggio) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", Instant.now().toString());
		body.put("status", status.value());
		body.put("errore", status.getReasonPhrase());
		body.put("messaggio", messaggio);
		return ResponseEntity.status(status).body(body);
	}
}
