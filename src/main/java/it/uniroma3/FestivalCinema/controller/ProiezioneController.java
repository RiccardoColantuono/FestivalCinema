package it.uniroma3.FestivalCinema.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.FestivalCinema.model.Festival;
import it.uniroma3.FestivalCinema.model.Proiezione;
import it.uniroma3.FestivalCinema.service.FestivalService;
import it.uniroma3.FestivalCinema.service.ProiezioneService;
import it.uniroma3.FestivalCinema.service.SalaService;

// Programmazione delle proiezioni (Sezione 4.3): caso d'uso transazionale
// multi-entita' gia' implementato in ProiezioneService.programmaProiezione(...);
// questo controller e' il solo strato di presentazione Thymeleaf per l'admin,
// raggiunto dai pulsanti presenti in festival/show.html.
@Controller
public class ProiezioneController {

	private final ProiezioneService proiezioneService;
	private final FestivalService festivalService;
	private final SalaService salaService;

	public ProiezioneController(ProiezioneService proiezioneService, FestivalService festivalService,
	                             SalaService salaService) {
		this.proiezioneService = proiezioneService;
		this.festivalService = festivalService;
		this.salaService = salaService;
	}

	@GetMapping("/admin/proiezioni/nuova")
	public String formNuova(@RequestParam Long festivalId, Model model) {
		// findByIdConFilm: la lista dei film e' LAZY su Festival (Sezione 8.1),
		// va quindi caricata con join fetch nella stessa transazione del service
		// invece di affidarsi all'Open Session In View.
		Festival festival = this.festivalService.findByIdConFilm(festivalId)
			.orElseThrow(() -> new IllegalArgumentException("Festival non trovato con id " + festivalId));
		model.addAttribute("festival", festival);
		model.addAttribute("filmDisponibili", festival.getFilm());
		model.addAttribute("sale", this.salaService.findAll());
		return "admin/proiezione/form";
	}

	@PostMapping("/admin/proiezioni")
	public String crea(@RequestParam Long festivalId, @RequestParam Long filmId, @RequestParam Long salaId,
	                    @RequestParam LocalDate data, @RequestParam LocalTime ora) {
		this.proiezioneService.programmaProiezione(festivalId, filmId, salaId, data, ora);
		return "redirect:/festival/" + festivalId;
	}

	@GetMapping("/admin/proiezioni/{id}/modifica")
	public String formModifica(@PathVariable Long id, Model model) {
		Optional<Proiezione> proiezioneOptional = this.proiezioneService.findById(id);
		if (proiezioneOptional.isEmpty()) {
			return "redirect:/festival";
		}
		Proiezione proiezione = proiezioneOptional.get();
		model.addAttribute("proiezione", proiezione);
		model.addAttribute("stati", Proiezione.Stato.values());
		return "admin/proiezione/modifica";
	}

	@PostMapping("/admin/proiezioni/{id}")
	public String aggiorna(@PathVariable Long id, @RequestParam Long festivalId,
	                        @RequestParam Proiezione.Stato stato, @RequestParam LocalDate data,
	                        @RequestParam LocalTime ora) {
		this.proiezioneService.aggiorna(id, stato, data, ora);
		return "redirect:/festival/" + festivalId;
	}

	@PostMapping("/admin/proiezioni/{id}/elimina")
	public String elimina(@PathVariable Long id, @RequestParam Long festivalId) {
		this.proiezioneService.elimina(id);
		return "redirect:/festival/" + festivalId;
	}
}
