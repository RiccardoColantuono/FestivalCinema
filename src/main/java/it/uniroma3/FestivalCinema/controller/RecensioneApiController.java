package it.uniroma3.FestivalCinema.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import it.uniroma3.FestivalCinema.dto.RecensioneDTO;
import it.uniroma3.FestivalCinema.model.Recensione;
import it.uniroma3.FestivalCinema.service.RecensioneService;
import jakarta.validation.Valid;

//  un utente puo' modificare o eliminare esclusivamente le
// proprie recensioni; l'autorizzazione fine (autore/ADMIN) e' verificata nel Service.
@RestController
@RequestMapping("/api/recensioni")
public class RecensioneApiController {

	private final RecensioneService recensioneService;

	public RecensioneApiController(RecensioneService recensioneService) {
		this.recensioneService = recensioneService;
	}

	@PutMapping("/{id}")
	public RecensioneDTO aggiorna(@PathVariable Long id, @Valid @RequestBody Recensione recensione,
	                               Authentication authentication) {
		Recensione aggiornata = this.recensioneService.aggiorna(id, recensione, authentication.getName());
		return RecensioneDTO.from(aggiornata);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> elimina(@PathVariable Long id, Authentication authentication) {
		boolean isAdmin = authentication.getAuthorities().stream()
			.anyMatch(a -> a.getAuthority().equals("ADMIN"));
		this.recensioneService.elimina(id, authentication.getName(), isAdmin);
		return ResponseEntity.noContent().build();
	}
}
