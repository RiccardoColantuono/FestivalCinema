package it.uniroma3.FestivalCinema.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	// Ricerca per titolo, genere o regista (Sezione 9), impaginata.
	@Transactional(readOnly = true)
	public Page<Film> cerca(String titolo, String genere, Integer anno, String regista, Pageable pageable) {
		return this.filmRepository.cercaConFiltri(titolo, genere, anno, regista, pageable);
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

	public Film salva(Film film, Long registaId) {
		return salva(film, registaId, null);
	}

	// L'overload con locandina serve al form multipart del sito Thymeleaf;
	// l'API REST (FilmApiController) continua a usare la versione senza file,
	// dato che la creazione via JSON non prevede upload nello stesso passaggio.
	@Transactional
	public Film salva(Film film, Long registaId, String locandina) {
		film.setId(null);
		Regista regista = this.registaRepository.findById(registaId)
			.orElseThrow(() -> new IllegalArgumentException("Regista non trovato con id " + registaId));
		film.setRegista(regista);
		film.setLocandina(locandina);
		return this.filmRepository.save(film);
	}

	public Film aggiorna(Long id, Film datiAggiornati, Long registaId) {
		return aggiorna(id, datiAggiornati, registaId, null);
	}

	@Transactional
	public Film aggiorna(Long id, Film datiAggiornati, Long registaId, String nuovaLocandina) {
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
		if (nuovaLocandina != null) {
			film.setLocandina(nuovaLocandina);
		}

		return film;
	}

	@Transactional
	public Film aggiornaLocandina(Long id, String locandina) {
		Film film = this.filmRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Film non trovato con id " + id));
		film.setLocandina(locandina);
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
