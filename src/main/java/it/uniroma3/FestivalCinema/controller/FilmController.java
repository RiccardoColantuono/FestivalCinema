package it.uniroma3.FestivalCinema.controller;

import java.beans.PropertyEditorSupport;
import java.security.Principal;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.FestivalCinema.model.Film;
import it.uniroma3.FestivalCinema.model.Recensione;
import it.uniroma3.FestivalCinema.model.Regista;
import it.uniroma3.FestivalCinema.service.FilmService;
import it.uniroma3.FestivalCinema.service.RecensioneService;
import it.uniroma3.FestivalCinema.service.RegistaService;
import jakarta.validation.Valid;

@Controller
public class FilmController {

	private final FilmService filmService;
	private final RecensioneService recensioneService;
	private final RegistaService registaService;

	public FilmController(FilmService filmService, RecensioneService recensioneService,
	                       RegistaService registaService) {
		this.filmService = filmService;
		this.recensioneService = recensioneService;
		this.registaService = registaService;
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

	// ===================== FUNZIONALITA' ADMIN (Sezione 4.3) =====================

	// Il form usa una <select> con l'id del regista come valore: questo binder
	// converte quel valore in un Regista gestito prima della validazione, cosi'
	// il vincolo @NotNull su Film.regista viene verificato sull'oggetto gia'
	// risolto (senza questo binder il campo arriverebbe sempre nullo).
	@InitBinder("film")
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Regista.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
				if (text == null || text.isBlank()) {
					setValue(null);
					return;
				}
				Regista regista = FilmController.this.registaService.findById(Long.valueOf(text))
					.orElseThrow(() -> new IllegalArgumentException("Regista non trovato con id " + text));
				setValue(regista);
			}

			@Override
			public String getAsText() {
				Regista regista = (Regista) getValue();
				return regista == null ? "" : regista.getId().toString();
			}
		});
	}

	@GetMapping("/admin/film/nuovo")
	public String formNuovo(Model model) {
		model.addAttribute("film", new Film());
		model.addAttribute("registi", this.registaService.findAll());
		return "admin/film/form";
	}

	@PostMapping("/admin/film")
	public String crea(@Valid @ModelAttribute("film") Film film, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("registi", this.registaService.findAll());
			return "admin/film/form";
		}
		Film salvato = this.filmService.salva(film, film.getRegista().getId());
		return "redirect:/film/" + salvato.getId();
	}

	@GetMapping("/admin/film/{id}/modifica")
	public String formModifica(@PathVariable Long id, Model model) {
		Optional<Film> filmOptional = this.filmService.findById(id);
		if (filmOptional.isEmpty()) {
			return "redirect:/film";
		}
		model.addAttribute("film", filmOptional.get());
		model.addAttribute("registi", this.registaService.findAll());
		return "admin/film/form";
	}

	@PostMapping("/admin/film/{id}")
	public String aggiorna(@PathVariable Long id, @Valid @ModelAttribute("film") Film filmForm,
	                        BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			filmForm.setId(id);
			model.addAttribute("registi", this.registaService.findAll());
			return "admin/film/form";
		}
		Film aggiornato = this.filmService.aggiorna(id, filmForm, filmForm.getRegista().getId());
		return "redirect:/film/" + aggiornato.getId();
	}

	@PostMapping("/admin/film/{id}/elimina")
	public String elimina(@PathVariable Long id) {
		this.filmService.elimina(id);
		return "redirect:/film";
	}
}
