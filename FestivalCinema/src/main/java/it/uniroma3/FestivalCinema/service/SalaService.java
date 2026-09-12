package it.uniroma3.FestivalCinema.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.FestivalCinema.model.Sala;
import it.uniroma3.FestivalCinema.repository.SalaRepository;

@Service
public class SalaService {

	private final SalaRepository salaRepository;

	public SalaService(SalaRepository salaRepository) {
		this.salaRepository = salaRepository;
	}

	@Transactional(readOnly = true)
	public List<Sala> findAll() {
		return this.salaRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Optional<Sala> findById(Long id) {
		return this.salaRepository.findById(id);
	}

	@Transactional
	public Sala salva(Sala sala) {
		sala.setId(null);
		return this.salaRepository.save(sala);
	}

	@Transactional
	public Sala aggiorna(Long id, Sala datiAggiornati) {
		Sala sala = this.salaRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Sala non trovata con id " + id));

		sala.setNome(datiAggiornati.getNome());
		sala.setIndirizzo(datiAggiornati.getIndirizzo());
		sala.setCapienza(datiAggiornati.getCapienza());

		return sala;
	}
}
