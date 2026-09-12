package it.uniroma3.FestivalCinema.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import it.uniroma3.FestivalCinema.dto.SalaDTO;
import it.uniroma3.FestivalCinema.model.Sala;
import it.uniroma3.FestivalCinema.service.SalaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/sale")
public class SalaApiController {

	private final SalaService salaService;

	public SalaApiController(SalaService salaService) {
		this.salaService = salaService;
	}

	@GetMapping
	public List<SalaDTO> tutte() {
		return this.salaService.findAll().stream().map(SalaDTO::from).toList();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public SalaDTO crea(@Valid @RequestBody Sala sala) {
		return SalaDTO.from(this.salaService.salva(sala));
	}

	@PutMapping("/{id}")
	public SalaDTO aggiorna(@PathVariable Long id, @Valid @RequestBody Sala sala) {
		return SalaDTO.from(this.salaService.aggiorna(id, sala));
	}
}
