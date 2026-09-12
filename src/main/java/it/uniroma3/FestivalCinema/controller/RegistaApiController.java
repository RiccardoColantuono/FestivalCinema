package it.uniroma3.FestivalCinema.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import it.uniroma3.FestivalCinema.dto.RegistaDTO;
import it.uniroma3.FestivalCinema.model.Regista;
import it.uniroma3.FestivalCinema.service.RegistaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/registi")
public class RegistaApiController {

	private final RegistaService registaService;

	public RegistaApiController(RegistaService registaService) {
		this.registaService = registaService;
	}

	@GetMapping
	public List<RegistaDTO> tutti() {
		return this.registaService.findAll().stream().map(RegistaDTO::from).toList();
	}

	@GetMapping("/{id}")
	public RegistaDTO dettaglio(@PathVariable Long id) {
		return RegistaDTO.from(this.registaService.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Regista non trovato con id " + id)));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public RegistaDTO crea(@Valid @RequestBody Regista regista) {
		return RegistaDTO.from(this.registaService.salva(regista));
	}

	@PutMapping("/{id}")
	public RegistaDTO aggiorna(@PathVariable Long id, @Valid @RequestBody Regista regista) {
		return RegistaDTO.from(this.registaService.aggiorna(id, regista));
	}
}
