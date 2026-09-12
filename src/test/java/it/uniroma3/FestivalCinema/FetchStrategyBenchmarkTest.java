package it.uniroma3.FestivalCinema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import it.uniroma3.FestivalCinema.model.Festival;
import it.uniroma3.FestivalCinema.model.Film;
import it.uniroma3.FestivalCinema.model.Regista;
import it.uniroma3.FestivalCinema.repository.FestivalRepository;
import it.uniroma3.FestivalCinema.repository.FilmRepository;
import it.uniroma3.FestivalCinema.repository.RegistaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/*
 * SEZIONE 8.2 - Analisi sperimentale dell'accesso ai dati.
 * =========================================================
 * Caso d'uso scelto: "caricamento di tutti i film di un festival con i
 * relativi registi" (uno degli esempi suggeriti dalla consegna).
 *
 * Confronta tre strategie sullo STESSO insieme di dati:
 *   1. LAZY/baseline : nessun JOIN FETCH. Film.regista e' pero' mappato EAGER
 *      (Sezione 8.1), quindi Hibernate carica comunque ogni regista, ma con
 *      una SELECT separata per ciascun film -> problema N+1.
 *   2. JOIN FETCH     : query JPQL con "JOIN FETCH f.regista" -> 1 sola query.
 *   3. EntityGraph    : equivalente dichiarativo del JOIN FETCH -> 1 sola query.
 *
 * Le query SQL vengono contate con le Hibernate Statistics (nessuna
 * infrastruttura di benchmarking esterna, come richiesto dalla consegna).
 *
 * ESEGUIBILE DURANTE LA PROVA ORALE con:
 *   mvn test -Dtest=FetchStrategyBenchmarkTest
 * (richiede il database PostgreSQL configurato in application.properties attivo)
 */
@SpringBootTest
public class FetchStrategyBenchmarkTest {

	// Numero di film di test creati per rendere visibile il problema N+1.
	// Con pochi film la differenza in ms e' trascurabile: il numero di query
	// SQL eseguite (colonna "Query SQL" nell'output) e' la metrica piu' solida.
	private static final int NUMERO_FILM = 60;

	@Autowired
	private FilmRepository filmRepository;

	@Autowired
	private FestivalRepository festivalRepository;

	@Autowired
	private RegistaRepository registaRepository;

	@PersistenceContext
	private EntityManager entityManager;

	private Long festivalId;
	private final List<Long> filmIdsCreati = new ArrayList<>();
	private final List<Long> registiIdsCreati = new ArrayList<>();

	@BeforeEach
	void creaDatiDiTest() {
		List<Film> filmCreati = new ArrayList<>();

		for (int i = 0; i < NUMERO_FILM; i++) {
			Regista regista = new Regista();
			regista.setNome("RegistaTest" + i);
			regista.setCognome("CognomeTest" + i);
			regista = this.registaRepository.save(regista);
			this.registiIdsCreati.add(regista.getId());

			Film film = new Film();
			film.setTitolo("Film di benchmark #" + i);
			film.setAnno(2020);
			film.setDurata(100);
			film.setGenere("Test");
			film.setPaeseProduzione("Italia");
			film.setRegista(regista);
			film = this.filmRepository.save(film);
			this.filmIdsCreati.add(film.getId());
			filmCreati.add(film);
		}

		Festival festival = new Festival();
		festival.setNome("Festival di benchmark (dati generati dal test)");
		festival.setAnno(2026);
		festival.setCitta("Roma");
		festival.setDataInizio(LocalDate.now());
		festival.setDataFine(LocalDate.now().plusDays(5));
		festival.setFilm(filmCreati);
		festival = this.festivalRepository.save(festival);
		this.festivalId = festival.getId();
	}

	@AfterEach
	void pulisciDatiDiTest() {
		this.festivalRepository.findById(this.festivalId).ifPresent(festival -> {
			festival.getFilm().clear();
			this.festivalRepository.save(festival);
			this.festivalRepository.delete(festival);
		});
		this.filmIdsCreati.forEach(id -> this.filmRepository.deleteById(id));
		this.registiIdsCreati.forEach(id -> this.registaRepository.deleteById(id));
		this.filmIdsCreati.clear();
		this.registiIdsCreati.clear();
	}

	@Test
	void confrontoStrategieFetchFilmDiUnFestivalConRegisti() {
		Statistics statistics = this.entityManager.unwrap(Session.class).getSessionFactory().getStatistics();
		statistics.setStatisticsEnabled(true);

		System.out.println();
		System.out.println("=== Test accesso ai film del festival (con registi) ===");
		System.out.println("Dati: 1 festival, " + NUMERO_FILM + " film, " + NUMERO_FILM + " registi");
		System.out.println();

		// STRATEGIA 1: baseline, nessun JOIN FETCH -> N+1 (regista e' EAGER)
		statistics.clear();
		long inizio1 = System.nanoTime();
		List<Film> filmBaseline = this.filmRepository.findByFestivalId(this.festivalId);
		filmBaseline.forEach(f -> f.getRegista().getNome()); // forza l'accesso, come farebbe una view
		long tempo1Ms = (System.nanoTime() - inizio1) / 1_000_000;
		long query1 = statistics.getPrepareStatementCount();
		stampaRisultato("Strategia 1: BASELINE (nessun JOIN FETCH)", filmBaseline.size(), query1, tempo1Ms);

		// STRATEGIA 2: JOIN FETCH esplicito -> 1 sola query
		statistics.clear();
		long inizio2 = System.nanoTime();
		List<Film> filmJoinFetch = this.filmRepository.findByFestivalIdWithRegistaJoinFetch(this.festivalId);
		filmJoinFetch.forEach(f -> f.getRegista().getNome());
		long tempo2Ms = (System.nanoTime() - inizio2) / 1_000_000;
		long query2 = statistics.getPrepareStatementCount();
		stampaRisultato("Strategia 2: JOIN FETCH", filmJoinFetch.size(), query2, tempo2Ms);

		// STRATEGIA 3: EntityGraph -> equivalente funzionale al JOIN FETCH
		statistics.clear();
		long inizio3 = System.nanoTime();
		List<Film> filmEntityGraph = this.filmRepository.findByFestivalIdWithRegistaEntityGraph(this.festivalId);
		filmEntityGraph.forEach(f -> f.getRegista().getNome());
		long tempo3Ms = (System.nanoTime() - inizio3) / 1_000_000;
		long query3 = statistics.getPrepareStatementCount();
		stampaRisultato("Strategia 3: ENTITY GRAPH", filmEntityGraph.size(), query3, tempo3Ms);

		System.out.println();
		System.out.println("Riepilogo: la baseline esegue " + query1 + " query SQL, JOIN FETCH ed"
			+ " EntityGraph ne eseguono " + query2 + ". Il numero di query della baseline"
			+ " cresce linearmente con il numero di film (N+1); quello delle altre due"
			+ " strategie resta costante.");
		System.out.println();

		// Le tre strategie devono restituire lo stesso numero di film...
		assertEquals(NUMERO_FILM, filmBaseline.size());
		assertEquals(NUMERO_FILM, filmJoinFetch.size());
		assertEquals(NUMERO_FILM, filmEntityGraph.size());

		// ...ma con un numero di query radicalmente diverso: il problema N+1 e'
		// dimostrato quando query1 e' sensibilmente maggiore di query2/query3.
		assertTrue(query1 > query2,
			"Ci si aspetta che la baseline esegua piu' query del JOIN FETCH (N+1)");
		assertEquals(1, query2, "Il JOIN FETCH deve risolvere il caso d'uso con una sola query");
		assertEquals(1, query3, "L'EntityGraph deve risolvere il caso d'uso con una sola query");
	}

	private void stampaRisultato(String nomeStrategia, int filmCaricati, long querySql, long tempoMs) {
		System.out.println(nomeStrategia);
		System.out.println("Film caricati: " + filmCaricati);
		System.out.println("Query SQL eseguite: " + querySql);
		System.out.println("Tempo: " + tempoMs + " ms");
		System.out.println();
	}
}
