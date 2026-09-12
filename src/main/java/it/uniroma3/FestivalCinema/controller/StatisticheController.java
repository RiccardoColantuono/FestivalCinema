package it.uniroma3.FestivalCinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import it.uniroma3.FestivalCinema.service.RecensioneService;

// Statistiche generali sulle recensioni: classifica dei film per voto medio.
// Le statistiche per singolo film (voto medio, distribuzione) sono invece
// mostrate direttamente in film/show.html (vedi FilmController.dettaglio).
@Controller
public class StatisticheController {

	private final RecensioneService recensioneService;

	public StatisticheController(RecensioneService recensioneService) {
		this.recensioneService = recensioneService;
	}

	@GetMapping("/statistiche")
	public String statistiche(Model model) {
		model.addAttribute("classifica", this.recensioneService.classificaFilmPerVotoMedio());
		return "statistiche";
	}
}
