package it.uniroma3.FestivalCinema.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.FestivalCinema.model.Festival;
import it.uniroma3.FestivalCinema.model.Film;
import it.uniroma3.FestivalCinema.repository.FestivalRepository;
import it.uniroma3.FestivalCinema.repository.FilmRepository;

@Service
public class FestivalService {

	private final FestivalRepository festivalRepository;
	private final FilmRepository filmRepository;

	public FestivalService(FestivalRepository festivalRepository, FilmRepository filmRepository) {
		this.festivalRepository = festivalRepository;
		this.filmRepository = filmRepository;
	}

	@Transactional(readOnly = true)
	public List<Festival> findAll() {
		return this.festivalRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Optional<Festival> findById(Long id) {
		return this.festivalRepository.findById(id);
	}

	// Dettaglio festival con film gia' caricati in un'unica query (Sezione 4.1:
	// "il dettaglio di un festival deve permettere di accedere ai film associati")
	@Transactional(readOnly = true)
	public Optional<Festival> findByIdConFilm(Long id) {
		return this.festivalRepository.findByIdWithFilmJoinFetch(id);
	}

	@Transactional(readOnly = true)
	public Optional<Festival> findByIdConProgramma(Long id) {
		return this.festivalRepository.findByIdWithProgrammaJoinFetch(id);
	}

	@Transactional
	public Festival salva(Festival festival) {
		festival.setId(null);
		if (festival.getDataFine().isBefore(festival.getDataInizio())) {
			throw new IllegalArgumentException("La data di fine non puo' precedere la data di inizio");
		}
		return this.festivalRepository.save(festival);
	}

	@Transactional
	public Festival aggiorna(Long id, Festival datiAggiornati) {
		Festival festival = this.festivalRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Festival non trovato con id " + id));

		festival.setNome(datiAggiornati.getNome());
		festival.setAnno(datiAggiornati.getAnno());
		festival.setCitta(datiAggiornati.getCitta());
		festival.setDataInizio(datiAggiornati.getDataInizio());
		festival.setDataFine(datiAggiornati.getDataFine());
		festival.setDescrizione(datiAggiornati.getDescrizione());

		return festival;
	}

	// Eliminazione di un festival (Sezione 4.3): impedita se esistono proiezioni
	// gia' programmate, per non lasciare dati orfani/incoerenti nel programma.
	@Transactional
	public void elimina(Long id) {
		Festival festival = this.festivalRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Festival non trovato con id " + id));

		if (!festival.getProiezioni().isEmpty()) {
			throw new IllegalStateException(
				"Impossibile eliminare il festival: esistono proiezioni gia' programmate");
		}

		festival.getFilm().clear();
		this.festivalRepository.delete(festival);
	}

	// Sezione 4.3: "associazione di un film a un festival"
	@Transactional
	public void aggiungiFilm(Long festivalId, Long filmId) {
		Festival festival = this.festivalRepository.findById(festivalId)
			.orElseThrow(() -> new IllegalArgumentException("Festival non trovato con id " + festivalId));
		Film film = this.filmRepository.findById(filmId)
			.orElseThrow(() -> new IllegalArgumentException("Film non trovato con id " + filmId));

		if (!festival.getFilm().contains(film)) {
			festival.getFilm().add(film);
		}
	}

	// Sezione 4.3: "eliminazione di un film da un festival" (rimuove solo
	// l'associazione, non il film ne' le eventuali proiezioni gia' programmate:
	// per coerenza si impedisce la rimozione se esistono proiezioni collegate)
	@Transactional
	public void rimuoviFilm(Long festivalId, Long filmId) {
		Festival festival = this.festivalRepository.findById(festivalId)
			.orElseThrow(() -> new IllegalArgumentException("Festival non trovato con id " + festivalId));
		Film film = this.filmRepository.findById(filmId)
			.orElseThrow(() -> new IllegalArgumentException("Film non trovato con id " + filmId));

		boolean haProiezioniNelFestival = festival.getProiezioni().stream()
			.anyMatch(p -> p.getFilm().equals(film));
		if (haProiezioniNelFestival) {
			throw new IllegalStateException(
				"Impossibile rimuovere il film: esistono proiezioni programmate in questo festival");
		}

		festival.getFilm().remove(film);
	}
}
