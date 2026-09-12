package it.uniroma3.FestivalCinema.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.FestivalCinema.model.Regista;
import it.uniroma3.FestivalCinema.repository.RegistaRepository;

@Service
public class RegistaService {

	private final RegistaRepository registaRepository;

	public RegistaService(RegistaRepository registaRepository) {
		this.registaRepository = registaRepository;
	}

	@Transactional(readOnly = true)
	public List<Regista> findAll() {
		return this.registaRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Optional<Regista> findById(Long id) {
		return this.registaRepository.findById(id);
	}

	@Transactional
	public Regista salva(Regista regista) {
		regista.setId(null);
		return this.registaRepository.save(regista);
	}

	@Transactional
	public Regista aggiorna(Long id, Regista datiAggiornati) {
		Regista regista = this.registaRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Regista non trovato con id " + id));

		regista.setNome(datiAggiornati.getNome());
		regista.setCognome(datiAggiornati.getCognome());
		regista.setDataNascita(datiAggiornati.getDataNascita());
		regista.setNazionalita(datiAggiornati.getNazionalita());

		return regista;
	}
}
