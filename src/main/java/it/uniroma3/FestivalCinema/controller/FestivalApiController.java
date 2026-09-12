package it.uniroma3.FestivalCinema.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import it.uniroma3.FestivalCinema.dto.FestivalDTO;
import it.uniroma3.FestivalCinema.dto.FilmDTO;
import it.uniroma3.FestivalCinema.dto.ProiezioneDTO;
import it.uniroma3.FestivalCinema.model.Festival;
import it.uniroma3.FestivalCinema.service.FestivalService;
import it.uniroma3.FestivalCinema.service.ProiezioneService;
import jakarta.validation.Valid;

// Sezione 10: endpoint REST usati dal frontend React
@RestController
@RequestMapping("/api/festival")
public class FestivalApiController {

	private final FestivalService festivalService;
	private final ProiezioneService proiezioneService;

	public FestivalApiController(FestivalService festivalService, ProiezioneService proiezioneService) {
		this.festivalService = festivalService;
		this.proiezioneService = proiezioneService;
	}

	// GET /api/festival
	@GetMapping
	public List<FestivalDTO> tuttiIFestival() {
		return this.festivalService.findAll().stream().map(FestivalDTO::from).toList();
	}

	// GET /api/festival/{id}
	@GetMapping("/{id}")
	public FestivalDTO dettaglio(@PathVariable Long id) {
		Festival festival = this.festivalService.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Festival non trovato con id " + id));
		return FestivalDTO.from(festival);
	}

	// GET /api/festival/{id}/movies
	@GetMapping("/{id}/movies")
	public List<FilmDTO> filmDelFestival(@PathVariable Long id) {
		Festival festival = this.festivalService.findByIdConFilm(id)
			.orElseThrow(() -> new IllegalArgumentException("Festival non trovato con id " + id));
		return festival.getFilm().stream().map(FilmDTO::from).toList();
	}

	// GET /api/festival/{id}/screenings - programma delle proiezioni (Sezione 9)
	@GetMapping("/{id}/screenings")
	public List<ProiezioneDTO> proiezioniDelFestival(@PathVariable Long id) {
		return this.proiezioneService.findByFestival(id).stream().map(ProiezioneDTO::from).toList();
	}

	// POST /api/festival - solo ADMIN (Sezione 4.3)
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public FestivalDTO crea(@Valid @RequestBody Festival festival) {
		return FestivalDTO.from(this.festivalService.salva(festival));
	}

	// PUT /api/festival/{id} - solo ADMIN
	@PutMapping("/{id}")
	public FestivalDTO aggiorna(@PathVariable Long id, @Valid @RequestBody Festival festival) {
		return FestivalDTO.from(this.festivalService.aggiorna(id, festival));
	}

	// POST /api/festival/{id}/movies/{filmId} - associa un film al festival
	@PostMapping("/{id}/movies/{filmId}")
	public ResponseEntity<Void> aggiungiFilm(@PathVariable Long id, @PathVariable Long filmId) {
		this.festivalService.aggiungiFilm(id, filmId);
		return ResponseEntity.noContent().build();
	}

	// DELETE /api/festival/{id}/movies/{filmId} - rimuove un film dal festival
	@DeleteMapping("/{id}/movies/{filmId}")
	public ResponseEntity<Void> rimuoviFilm(@PathVariable Long id, @PathVariable Long filmId) {
		this.festivalService.rimuoviFilm(id, filmId);
		return ResponseEntity.noContent().build();
	}
}
