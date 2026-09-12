package it.uniroma3.FestivalCinema.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.FestivalCinema.model.Festival;
import it.uniroma3.FestivalCinema.model.Film;
import it.uniroma3.FestivalCinema.model.Proiezione;
import it.uniroma3.FestivalCinema.model.Sala;
import it.uniroma3.FestivalCinema.repository.FestivalRepository;
import it.uniroma3.FestivalCinema.repository.FilmRepository;
import it.uniroma3.FestivalCinema.repository.ProiezioneRepository;
import it.uniroma3.FestivalCinema.repository.SalaRepository;

/*
 * DISCUSSIONE TRANSAZIONALE (Sezione 7)
 * =======================================
 * programmaProiezione(...) e' il caso d'uso che coinvolge piu' entita' e
 * repository richiesto dalla consegna:
 *   1. recupero del festival        (FestivalRepository)
 *   2. recupero del film            (FilmRepository)
 *   3. recupero della sala          (SalaRepository)
 *   4. verifica della disponibilita' della sala (ProiezioneRepository)
 *   5. creazione della proiezione   (ProiezioneRepository)
 *   6. aggiornamento delle associazioni coinvolte (le liste festival.proiezioni,
 *      film.proiezioni, sala.proiezioni sono lato "mappedBy": basta l'insert
 *      della riga in proiezione con le tre FK per mantenerle coerenti)
 *
 * Atomicita': tutto il metodo e' racchiuso in un'unica transazione @Transactional.
 * Se una qualunque delle operazioni fallisce (es. sala non trovata, sala occupata,
 * violazione di un vincolo), Spring esegue il rollback automatico e nessuna
 * scrittura parziale rimane nel DB (requisito "lo stato del sistema deve
 * rimanere consistente").
 *
 * Isolamento: usiamo READ_COMMITTED (il default di PostgreSQL) per il caso
 * generale, che offre buone prestazioni in lettura/scrittura concorrente.
 * Il controllo "verifica disponibilita' sala" e' pero' soggetto in teoria a una
 * race condition: due transazioni concorrenti potrebbero entrambe leggere
 * "sala libera" prima che l'altra abbia fatto commit, e programmare due
 * proiezioni sovrapposte. Per un'operazione di scrittura critica come questa,
 * alziamo esplicitamente l'isolamento a SERIALIZABLE solo su questo metodo:
 * PostgreSQL rileva il conflitto scrivente-scrivente e fa fallire una delle
 * due transazioni con un'eccezione di serializzazione, che il chiamante puo'
 * intercettare e far ritentare. La scelta e' un compromesso deliberato:
 * minore concorrenza su QUESTA sola operazione, in cambio di correttezza
 * garantita, mentre tutte le altre operazioni di lettura/scrittura del
 * sistema restano a READ_COMMITTED per non penalizzare le prestazioni globali.
 */
@Service
public class ProiezioneService {

	private final ProiezioneRepository proiezioneRepository;
	private final FestivalRepository festivalRepository;
	private final FilmRepository filmRepository;
	private final SalaRepository salaRepository;

	public ProiezioneService(ProiezioneRepository proiezioneRepository,
	                          FestivalRepository festivalRepository,
	                          FilmRepository filmRepository,
	                          SalaRepository salaRepository) {
		this.proiezioneRepository = proiezioneRepository;
		this.festivalRepository = festivalRepository;
		this.filmRepository = filmRepository;
		this.salaRepository = salaRepository;
	}

	@Transactional(readOnly = true)
	public List<Proiezione> findByFestival(Long festivalId) {
		// join fetch mirato: programma completo in un'unica query (Sezione 8.2)
		return this.proiezioneRepository.findByFestivalIdWithFilmESalaJoinFetch(festivalId);
	}

	@Transactional(readOnly = true)
	public Optional<Proiezione> findById(Long id) {
		return this.proiezioneRepository.findById(id);
	}

	@Transactional(isolation = Isolation.SERIALIZABLE)
	public Proiezione programmaProiezione(Long festivalId, Long filmId, Long salaId,
	                                       java.time.LocalDate data, java.time.LocalTime ora) {

		// 1. recupero del festival
		Festival festival = this.festivalRepository.findById(festivalId)
			.orElseThrow(() -> new IllegalArgumentException("Festival non trovato con id " + festivalId));

		// 2. recupero del film
		Film film = this.filmRepository.findById(filmId)
			.orElseThrow(() -> new IllegalArgumentException("Film non trovato con id " + filmId));

		// 3. recupero della sala
		Sala sala = this.salaRepository.findById(salaId)
			.orElseThrow(() -> new IllegalArgumentException("Sala non trovata con id " + salaId));

		// 4. verifica della disponibilita' della sala (Sezione 4.3: vincolo di consistenza)
		List<Proiezione> conflitti = this.proiezioneRepository.findConflittiSala(salaId, data, ora, null);
		if (!conflitti.isEmpty()) {
			throw new IllegalStateException(
				"La sala '" + sala.getNome() + "' e' gia' occupata il " + data + " alle " + ora);
		}

		// 5. creazione della proiezione
		Proiezione proiezione = new Proiezione();
		proiezione.setFestival(festival);
		proiezione.setFilm(film);
		proiezione.setSala(sala);
		proiezione.setData(data);
		proiezione.setOra(ora);
		proiezione.setStato(Proiezione.Stato.SCHEDULED);

		// 6. aggiornamento delle associazioni: la sola persistenza della riga con
		// le tre FK e' sufficiente; le collezioni lato "mappedBy" (festival.proiezioni,
		// film.proiezioni, sala.proiezioni) verranno lette correttamente alla
		// prossima query poiche' sono LAZY/EAGER derivate dalla FK, non duplicate.
		return this.proiezioneRepository.save(proiezione);
	}

	@Transactional
	public Proiezione aggiorna(Long id, Proiezione.Stato nuovoStato, java.time.LocalDate data,
	                            java.time.LocalTime ora) {
		Proiezione proiezione = this.proiezioneRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Proiezione non trovata con id " + id));

		if (data != null && ora != null) {
			List<Proiezione> conflitti = this.proiezioneRepository
				.findConflittiSala(proiezione.getSala().getId(), data, ora, id);
			if (!conflitti.isEmpty()) {
				throw new IllegalStateException("Conflitto: sala gia' occupata in quell'orario");
			}
			proiezione.setData(data);
			proiezione.setOra(ora);
		}
		if (nuovoStato != null) {
			proiezione.setStato(nuovoStato);
		}

		return proiezione;
	}

	@Transactional
	public void elimina(Long id) {
		Proiezione proiezione = this.proiezioneRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Proiezione non trovata con id " + id));
		this.proiezioneRepository.delete(proiezione);
	}
}
