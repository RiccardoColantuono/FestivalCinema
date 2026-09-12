package it.uniroma3.FestivalCinema.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.uniroma3.FestivalCinema.model.Recensione;

public interface RecensioneRepository extends JpaRepository<Recensione, Long> {

	List<Recensione> findByFilmId(Long filmId);

	long countByFilmId(Long filmId);

	// Usata dal Service per impedire piu' di una recensione dello stesso utente
	// sullo stesso film (Sezione 3, 4.2), oltre al vincolo UNIQUE a livello DB.
	Optional<Recensione> findByFilmIdAndAutoreUsername(Long filmId, String autoreUsername);

	// Statistiche sulle recensioni di un film: voto medio e distribuzione (quante
	// recensioni per ciascun voto da 1 a 5), usate nella pagina di dettaglio film.
	@Query("SELECT AVG(r.voto) FROM Recensione r WHERE r.film.id = :filmId")
	Double mediaVotoPerFilm(@Param("filmId") Long filmId);

	@Query("SELECT r.voto, COUNT(r) FROM Recensione r WHERE r.film.id = :filmId GROUP BY r.voto")
	List<Object[]> distribuzioneVotiPerFilm(@Param("filmId") Long filmId);

	// Classifica dei film per voto medio (statistiche generali, Sezione 4.1/9):
	// solo i film con almeno una recensione, ordinati dal piu' apprezzato. Ogni
	// riga e' [Film, Double mediaVoto, Long numeroRecensioni].
	@Query("SELECT f, AVG(r.voto), COUNT(r) FROM Recensione r JOIN r.film f GROUP BY f ORDER BY AVG(r.voto) DESC")
	List<Object[]> classificaFilmPerVotoMedio();
}
