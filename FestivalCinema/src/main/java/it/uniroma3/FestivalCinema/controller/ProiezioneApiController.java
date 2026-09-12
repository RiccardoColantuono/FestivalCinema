package it.uniroma3.FestivalCinema.controller;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import it.uniroma3.FestivalCinema.dto.ProiezioneDTO;
import it.uniroma3.FestivalCinema.model.Proiezione;
import it.uniroma3.FestivalCinema.service.ProiezioneService;

// Sezione 4.3: programmazione/modifica/cancellazione proiezioni, riservate ad ADMIN
// (vedi SecurityConfig). E' il controller che espone il caso d'uso transazionale
// multi-entita' descritto in ProiezioneService.programmaProiezione(...).
@RestController
@RequestMapping("/api/proiezioni")
public class ProiezioneApiController {

	private final ProiezioneService proiezioneService;

	public ProiezioneApiController(ProiezioneService proiezioneService) {
		this.proiezioneService = proiezioneService;
	}

	public record NuovaProiezioneRequest(Long festivalId, Long filmId, Long salaId,
	                                      LocalDate data, LocalTime ora) {
	}

	public record AggiornaProiezioneRequest(Proiezione.Stato stato, LocalDate data, LocalTime ora) {
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ProiezioneDTO programma(@RequestBody NuovaProiezioneRequest richiesta) {
		Proiezione proiezione = this.proiezioneService.programmaProiezione(
			richiesta.festivalId(), richiesta.filmId(), richiesta.salaId(),
			richiesta.data(), richiesta.ora());
		return ProiezioneDTO.from(proiezione);
	}

	@PutMapping("/{id}")
	public ProiezioneDTO aggiorna(@PathVariable Long id, @RequestBody AggiornaProiezioneRequest richiesta) {
		Proiezione proiezione = this.proiezioneService.aggiorna(id, richiesta.stato(),
			richiesta.data(), richiesta.ora());
		return ProiezioneDTO.from(proiezione);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> elimina(@PathVariable Long id) {
		this.proiezioneService.elimina(id);
		return ResponseEntity.noContent().build();
	}
}
