package it.uniroma3.FestivalCinema.controller;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Sezione 10: la SPA React riusa l'autenticazione a sessione gia' esposta per il
// sito Thymeleaf (POST /login, POST /logout, entrambi gia' pubblici e ora anche
// abilitati in CORS, vedi CorsConfig/SecurityConfig). Questo endpoint e' il solo
// modo per il frontend di sapere, dopo il login, chi e' l'utente corrente e se e'
// ADMIN, dato che non riceve alcuna pagina Thymeleaf da cui dedurlo.
@RestController
@RequestMapping("/api")
public class AuthApiController {

	@GetMapping("/me")
	public Map<String, Object> me(Authentication authentication) {
		boolean autenticato = authentication != null && authentication.isAuthenticated()
			&& !"anonymousUser".equals(authentication.getPrincipal());

		if (!autenticato) {
			return Map.of("autenticato", false);
		}

		boolean admin = authentication.getAuthorities().stream()
			.anyMatch(a -> a.getAuthority().equals("ADMIN"));

		return Map.of(
			"autenticato", true,
			"username", authentication.getName(),
			"admin", admin
		);
	}
}
