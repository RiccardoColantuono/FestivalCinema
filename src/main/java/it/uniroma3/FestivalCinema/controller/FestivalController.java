package it.uniroma3.FestivalCinema.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.FestivalCinema.model.Festival;
import it.uniroma3.FestivalCinema.model.Film;
import it.uniroma3.FestivalCinema.service.FestivalService;
import it.uniroma3.FestivalCinema.service.FilmService;
import it.uniroma3.FestivalCinema.service.ProiezioneService;
import jakarta.validation.Valid;

@Controller
public class FestivalController {

	private final FestivalService festivalService;
	private final ProiezioneService proiezioneService;
	private final FilmService filmService;

	public FestivalController(FestivalService festivalService, ProiezioneService proiezioneService,
	                           FilmService filmService) {
		this.festivalService = festivalService;
		this.proiezioneService = proiezioneService;
		this.filmService = filmService;
	}

	// ===================== FUNZIONALITA' PUBBLICHE (Sezione 4.1) =====================

	@GetMapping("/festival")
	public String elenco(Model model) {
		model.addAttribute("festival", this.festivalService.findAll());
		return "festival/list";
	}

	@GetMapping("/festival/{id}")
	public String dettaglio(@PathVariable Long id, Model model) {
		Festival festival = this.festivalService.findByIdConFilm(id)
			.orElseThrow(() -> new IllegalArgumentException("Festival non trovato con id " + id));
		model.addAttribute("festival", festival);
		model.addAttribute("proiezioni", this.proiezioneService.findByFestival(id));

		List<Film> filmDisponibili = this.filmService.findAll();
		filmDisponibili.removeAll(festival.getFilm());
		model.addAttribute("filmDisponibili", filmDisponibili);

		return "festival/show";
	}

	// ===================== FUNZIONALITA' ADMIN (Sezione 4.3) =====================

	@GetMapping("/admin/festival/nuovo")
	public String formNuovo(Model model) {
		model.addAttribute("festival", new Festival());
		return "admin/festival/form";
	}

	@PostMapping("/admin/festival")
	public String crea(@Valid @ModelAttribute("festival") Festival festival, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "admin/festival/form";
		}
		Festival salvato = this.festivalService.salva(festival);
		return "redirect:/festival/" + salvato.getId();
	}

	@GetMapping("/admin/festival/{id}/modifica")
	public String formModifica(@PathVariable Long id, Model model) {
		Optional<Festival> festivalOptional = this.festivalService.findById(id);
		if (festivalOptional.isEmpty()) {
			return "redirect:/festival";
		}
		model.addAttribute("festival", festivalOptional.get());
		return "admin/festival/form";
	}

	@PostMapping("/admin/festival/{id}")
	public String aggiorna(@PathVariable Long id, @Valid @ModelAttribute("festival") Festival festivalForm,
	                        BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			festivalForm.setId(id);
			return "admin/festival/form";
		}
		Festival aggiornato = this.festivalService.aggiorna(id, festivalForm);
		return "redirect:/festival/" + aggiornato.getId();
	}

	@PostMapping("/admin/festival/{id}/elimina")
	public String elimina(@PathVariable Long id) {
		this.festivalService.elimina(id);
		return "redirect:/festival";
	}

	// Associazione/rimozione di un film dal programma del festival (Sezione 4.3).
	@PostMapping("/admin/festival/{id}/film")
	public String aggiungiFilm(@PathVariable Long id, @RequestParam Long filmId) {
		this.festivalService.aggiungiFilm(id, filmId);
		return "redirect:/festival/" + id;
	}

	@PostMapping("/admin/festival/{id}/film/{filmId}/elimina")
	public String rimuoviFilm(@PathVariable Long id, @PathVariable Long filmId) {
		this.festivalService.rimuoviFilm(id, filmId);
		return "redirect:/festival/" + id;
	}
}
