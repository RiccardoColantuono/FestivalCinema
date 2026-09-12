package it.uniroma3.FestivalCinema.controller;

import java.security.Principal;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.FestivalCinema.model.Film;
import it.uniroma3.FestivalCinema.model.Recensione;
import it.uniroma3.FestivalCinema.service.FilmService;
import it.uniroma3.FestivalCinema.service.RecensioneService;

@Controller
public class FilmController {

	private final FilmService filmService;
	private final RecensioneService recensioneService;

	public FilmController(FilmService filmService, RecensioneService recensioneService) {
		this.filmService = filmService;
		this.recensioneService = recensioneService;
	}

	// ===================== FUNZIONALITA' PUBBLICHE (Sezione 4.1) =====================

	@GetMapping("/film")
	public String elenco(@RequestParam(required = false) String titolo,
	                      @RequestParam(required = false) String genere,
	                      @RequestParam(required = false) Integer anno,
	                      Model model) {
		model.addAttribute("film", this.filmService.cerca(titolo, genere, anno));
		return "film/list";
	}

	@GetMapping("/film/{id}")
	public String dettaglio(@PathVariable Long id, Model model) {
		Film film = this.filmService.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Film non trovato con id " + id));
		model.addAttribute("film", film);
		model.addAttribute("recensioni", this.recensioneService.findByFilm(id));
		model.addAttribute("nuovaRecensione", new Recensione());
		return "film/show";
	}

	// ===================== FUNZIONALITA' UTENTI REGISTRATI (Sezione 4.2) =====================

	@PostMapping("/film/{id}/recensioni")
	public String inserisciRecensione(@PathVariable Long id, Recensione recensione, Principal principal) {
		this.recensioneService.salva(recensione, id, principal.getName());
		return "redirect:/film/" + id;
	}

	@PostMapping("/recensioni/{recensioneId}/elimina")
	public String eliminaRecensione(@PathVariable Long recensioneId, @RequestParam Long filmId,
	                                 Authentication authentication) {
		boolean isAdmin = authentication.getAuthorities().stream()
			.anyMatch(a -> a.getAuthority().equals("ADMIN"));
		this.recensioneService.elimina(recensioneId, authentication.getName(), isAdmin);
		return "redirect:/film/" + filmId;
	}
}
