package it.uniroma3.FestivalCinema.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.FestivalCinema.model.Film;
import it.uniroma3.FestivalCinema.model.Recensione;
import it.uniroma3.FestivalCinema.model.Utente;
import it.uniroma3.FestivalCinema.repository.FilmRepository;
import it.uniroma3.FestivalCinema.repository.RecensioneRepository;
import it.uniroma3.FestivalCinema.repository.UtenteRepository;

@Service
public class RecensioneService {

	private final RecensioneRepository recensioneRepository;
	private final FilmRepository filmRepository;
	private final UtenteRepository utenteRepository;

	public RecensioneService(RecensioneRepository recensioneRepository,
	                          FilmRepository filmRepository,
	                          UtenteRepository utenteRepository) {
		this.recensioneRepository = recensioneRepository;
		this.filmRepository = filmRepository;
		this.utenteRepository = utenteRepository;
	}

	@Transactional(readOnly = true)
	public List<Recensione> findByFilm(Long filmId) {
		return this.recensioneRepository.findByFilmId(filmId);
	}

	@Transactional(readOnly = true)
	public Optional<Recensione> findById(Long id) {
		return this.recensioneRepository.findById(id);
	}

	// Sezione 4.2: al massimo una recensione per utente per film.
	@Transactional
	public Recensione salva(Recensione recensione, Long filmId, String usernameAutore) {
		recensione.setId(null);

		Film film = this.filmRepository.findById(filmId)
			.orElseThrow(() -> new IllegalArgumentException("Film non trovato con id " + filmId));

		Utente autore = this.utenteRepository.findByUsername(usernameAutore)
			.orElseThrow(() -> new IllegalArgumentException("Utente non trovato: " + usernameAutore));

		if (this.recensioneRepository.findByFilmIdAndAutoreUsername(filmId, usernameAutore).isPresent()) {
			throw new IllegalStateException("Hai gia' inserito una recensione per questo film");
		}

		recensione.setFilm(film);
		recensione.setAutore(autore);
		recensione.setData(LocalDate.now());

		return this.recensioneRepository.save(recensione);
	}

	// Solo l'autore puo' modificare la propria recensione (Sezione 4.2, 5)
	@Transactional
	public Recensione aggiorna(Long id, Recensione datiAggiornati, String usernameRichiedente) {
		Recensione recensione = this.recensioneRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Recensione non trovata con id " + id));

		if (!recensione.getAutore().getUsername().equals(usernameRichiedente)) {
			throw new AccessDeniedException("Non puoi modificare una recensione scritta da un altro utente.");
		}

		recensione.setVoto(datiAggiornati.getVoto());
		recensione.setTesto(datiAggiornati.getTesto());
		recensione.setData(LocalDate.now());

		return recensione;
	}

	// Autore o ADMIN possono eliminare (Sezione 4.2, 5)
	@Transactional
	public void elimina(Long id, String usernameRichiedente, boolean isAdmin) {
		Recensione recensione = this.recensioneRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Recensione non trovata con id " + id));

		boolean isAutore = recensione.getAutore().getUsername().equals(usernameRichiedente);
		if (!isAutore && !isAdmin) {
			throw new AccessDeniedException("Non puoi eliminare una recensione scritta da un altro utente.");
		}

		this.recensioneRepository.delete(recensione);
	}
}
