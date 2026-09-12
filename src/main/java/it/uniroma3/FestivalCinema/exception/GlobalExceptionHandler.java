package it.uniroma3.FestivalCinema.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

// Gestione centralizzata degli errori per i controller Thymeleaf (Sezione 11:
// "gestione degli errori"). La controparte per i controller REST e' in ApiExceptionHandler.
@ControllerAdvice(annotations = org.springframework.stereotype.Controller.class)
public class GlobalExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String gestisciEntitaNonTrovata(IllegalArgumentException ex, Model model) {
		model.addAttribute("messaggio", ex.getMessage());
		return "error/404";
	}

	@ExceptionHandler(IllegalStateException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public String gestisciConflitto(IllegalStateException ex, Model model) {
		model.addAttribute("messaggio", ex.getMessage());
		return "error/conflitto";
	}

	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public String gestisciAccessoNegato(AccessDeniedException ex, Model model) {
		model.addAttribute("messaggio", ex.getMessage());
		return "error/403";
	}

	@ExceptionHandler(UsernameGiaRegistratoException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public String gestisciUsernameDuplicato(UsernameGiaRegistratoException ex, Model model) {
		model.addAttribute("messaggio", ex.getMessage());
		return "error/conflitto";
	}
}
