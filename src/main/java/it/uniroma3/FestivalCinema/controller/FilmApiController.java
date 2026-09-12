package it.uniroma3.FestivalCinema.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.FestivalCinema.dto.FilmDTO;
import it.uniroma3.FestivalCinema.dto.PaginaDTO;
import it.uniroma3.FestivalCinema.dto.RecensioneDTO;
import it.uniroma3.FestivalCinema.dto.StatisticheRecensioniDTO;
import it.uniroma3.FestivalCinema.model.Film;
import it.uniroma3.FestivalCinema.model.Recensione;
import it.uniroma3.FestivalCinema.service.FileStorageService;
import it.uniroma3.FestivalCinema.service.FilmService;
import it.uniroma3.FestivalCinema.service.RecensioneService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class FilmApiController {

	private static final int DIMENSIONE_PAGINA = 12;

	private final FilmService filmService;
	private final RecensioneService recensioneService;
	private final FileStorageService fileStorageService;

	public FilmApiController(FilmService filmService, RecensioneService recensioneService,
	                          FileStorageService fileStorageService) {
		this.filmService = filmService;
		this.recensioneService = recensioneService;
		this.fileStorageService = fileStorageService;
	}

	// GET /api/movies?titolo=&genere=&anno=&regista=&page=&size=  (ricerca impaginata, Sezione 9)
	@GetMapping("/movies")
	public PaginaDTO<FilmDTO> cercaFilm(@RequestParam(required = false) String titolo,
	                                     @RequestParam(required = false) String genere,
	                                     @RequestParam(required = false) Integer anno,
	                                     @RequestParam(required = false) String regista,
	                                     @RequestParam(defaultValue = "0") int page,
	                                     @RequestParam(defaultValue = "" + DIMENSIONE_PAGINA) int size) {
		var risultato = this.filmService.cerca(titolo, genere, anno, regista,
			PageRequest.of(page, size, Sort.by("titolo")));
		return PaginaDTO.from(risultato, FilmDTO::from);
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

	// GET /api/movies/{id}/statistiche - statistiche sulle recensioni del film (voto medio, distribuzione)
	@GetMapping("/movies/{id}/statistiche")
	public StatisticheRecensioniDTO statistiche(@PathVariable Long id) {
		return this.recensioneService.statistichePerFilm(id);
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

	// POST /api/movies/{id}/locandina (multipart) - upload della locandina, solo ADMIN
	@PostMapping("/movies/{id}/locandina")
	public FilmDTO caricaLocandina(@PathVariable Long id, @RequestParam MultipartFile file) {
		String nomeFile = this.fileStorageService.salva(file);
		return FilmDTO.from(this.filmService.aggiornaLocandina(id, nomeFile));
	}
}
