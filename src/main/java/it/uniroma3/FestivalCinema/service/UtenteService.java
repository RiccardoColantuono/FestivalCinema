package it.uniroma3.FestivalCinema.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.FestivalCinema.exception.UsernameGiaRegistratoException;
import it.uniroma3.FestivalCinema.model.Utente;
import it.uniroma3.FestivalCinema.repository.UtenteRepository;

@Service
public class UtenteService {

	private final UtenteRepository utenteRepository;
	private final PasswordEncoder passwordEncoder;

	public UtenteService(UtenteRepository utenteRepository, PasswordEncoder passwordEncoder) {
		this.utenteRepository = utenteRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional(readOnly = true)
	public Optional<Utente> findByUsername(String username) {
		return this.utenteRepository.findByUsername(username);
	}

	// Registrazione pubblica: ruolo sempre forzato a USER, la password viene
	// hashata prima di essere persistita (mai in chiaro nel DB).
	@Transactional
	public Utente registra(String username, String passwordInChiaro) {
		if (this.utenteRepository.existsByUsername(username)) {
			throw new UsernameGiaRegistratoException(username);
		}

		Utente utente = new Utente();
		utente.setUsername(username);
		utente.setPassword(this.passwordEncoder.encode(passwordInChiaro));
		utente.setRuolo(Utente.Ruolo.USER.name());

		return this.utenteRepository.save(utente);
	}
}
