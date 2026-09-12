package it.uniroma3.FestivalCinema.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.uniroma3.FestivalCinema.dto.FilmConMediaDTO;
import it.uniroma3.FestivalCinema.service.RecensioneService;

// Sezione 9/10: classifica dei film per voto medio, consumata dalla SPA React.
// Le statistiche per singolo film sono invece esposte da
// FilmApiController.statistiche (GET /api/movies/{id}/statistiche).
@RestController
@RequestMapping("/api/statistiche")
public class StatisticheApiController {

	private final RecensioneService recensioneService;

	public StatisticheApiController(RecensioneService recensioneService) {
		this.recensioneService = recensioneService;
	}

	@GetMapping("/classifica")
	public List<FilmConMediaDTO> classifica() {
		return this.recensioneService.classificaFilmPerVotoMedio();
	}
}
