package it.uniroma3.FestivalCinema.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.FestivalCinema.model.Film;
import it.uniroma3.FestivalCinema.model.Regista;
import it.uniroma3.FestivalCinema.repository.FilmRepository;
import it.uniroma3.FestivalCinema.repository.RegistaRepository;

@Service
public class FilmService {

	private final FilmRepository filmRepository;
	private final RegistaRepository registaRepository;

	public FilmService(FilmRepository filmRepository, RegistaRepository registaRepository) {
		this.filmRepository = filmRepository;
		this.registaRepository = registaRepository;
	}

	@Transactional(readOnly = true)
	public List<Film> findAll() {
		return this.filmRepository.findAll();
	}

	@Transactional(readOnly = true)
	public List<Film> cerca(String titolo, String genere, Integer anno) {
		return this.filmRepository.cercaConFiltri(titolo, genere, anno);
	}

	@Transactional(readOnly = true)
	public Optional<Film> findById(Long id) {
		return this.filmRepository.findById(id);
	}

	@Transactional(readOnly = true)
	public List<Film> findByFestival(Long festivalId) {
		// join fetch: evita N+1 quando si mostra l'elenco film di un festival
		// con il nome del regista (Sezione 8.2)
		return this.filmRepository.findByFestivalIdWithRegistaJoinFetch(festivalId);
	}

	@Transactional
	public Film salva(Film film, Long registaId) {
		film.setId(null);
		Regista regista = this.registaRepository.findById(registaId)
			.orElseThrow(() -> new IllegalArgumentException("Regista non trovato con id " + registaId));
		film.setRegista(regista);
		return this.filmRepository.save(film);
	}

	@Transactional
	public Film aggiorna(Long id, Film datiAggiornati, Long registaId) {
		Film film = this.filmRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Film non trovato con id " + id));

		film.setTitolo(datiAggiornati.getTitolo());
		film.setAnno(datiAggiornati.getAnno());
		film.setDurata(datiAggiornati.getDurata());
		film.setGenere(datiAggiornati.getGenere());
		film.setPaeseProduzione(datiAggiornati.getPaeseProduzione());

		if (registaId != null) {
			Regista regista = this.registaRepository.findById(registaId)
				.orElseThrow(() -> new IllegalArgumentException("Regista non trovato con id " + registaId));
			film.setRegista(regista);
		}

		return film;
	}

	// Eliminazione di un film (Sezione 4.3): impedita se esistono proiezioni o
	// recensioni collegate; rimossa l'eventuale associazione con i festival.
	@Transactional
	public void elimina(Long id) {
		Film film = this.filmRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Film non trovato con id " + id));

		if (!film.getProiezioni().isEmpty()) {
			throw new IllegalStateException(
				"Impossibile eliminare il film: esistono proiezioni gia' programmate");
		}
		if (!film.getRecensioni().isEmpty()) {
			throw new IllegalStateException(
				"Impossibile eliminare il film: esistono recensioni associate");
		}

		film.getFestival().forEach(festival -> festival.getFilm().remove(film));
		this.filmRepository.delete(film);
	}
}
