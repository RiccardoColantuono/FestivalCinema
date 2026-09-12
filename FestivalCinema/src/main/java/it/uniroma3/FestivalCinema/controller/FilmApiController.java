package it.uniroma3.FestivalCinema.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import it.uniroma3.FestivalCinema.dto.FilmDTO;
import it.uniroma3.FestivalCinema.dto.RecensioneDTO;
import it.uniroma3.FestivalCinema.model.Film;
import it.uniroma3.FestivalCinema.model.Recensione;
import it.uniroma3.FestivalCinema.service.FilmService;
import it.uniroma3.FestivalCinema.service.RecensioneService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class FilmApiController {

	private final FilmService filmService;
	private final RecensioneService recensioneService;

	public FilmApiController(FilmService filmService, RecensioneService recensioneService) {
		this.filmService = filmService;
		this.recensioneService = recensioneService;
	}

	// GET /api/movies?titolo=&genere=&anno=  (elenco/ricerca, Sezione 9)
	@GetMapping("/movies")
	public List<FilmDTO> cercaFilm(@RequestParam(required = false) String titolo,
	                                @RequestParam(required = false) String genere,
	                                @RequestParam(required = false) Integer anno) {
		return this.filmService.cerca(titolo, genere, anno).stream().map(FilmDTO::from).toList();
	}

	// GET /api/movies/{id}
	@GetMapping("/movies/{id}")
	public FilmDTO dettaglio(@PathVariable Long id) {
		Film film = this.filmService.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Film non trovato con id " + id));
		return FilmDTO.from(film);
	}

	// GET /api/movies/{id}/reviews
	@GetMapping("/movies/{id}/reviews")
	public List<RecensioneDTO> recensioniDelFilm(@PathVariable Long id) {
		return this.recensioneService.findByFilm(id).stream().map(RecensioneDTO::from).toList();
	}

	// POST /api/movies/{id}/reviews - richiede utente autenticato (Sezione 4.2)
	@PostMapping("/movies/{id}/reviews")
	@ResponseStatus(HttpStatus.CREATED)
	public RecensioneDTO inserisciRecensione(@PathVariable Long id,
	                                          @Valid @RequestBody Recensione recensione,
	                                          Authentication authentication) {
		Recensione salvata = this.recensioneService.salva(recensione, id, authentication.getName());
		return RecensioneDTO.from(salvata);
	}

	// POST /api/movies - solo ADMIN (Sezione 4.3)
	@PostMapping("/movies")
	@ResponseStatus(HttpStatus.CREATED)
	public FilmDTO crea(@Valid @RequestBody Film film, @RequestParam Long registaId) {
		return FilmDTO.from(this.filmService.salva(film, registaId));
	}
}
