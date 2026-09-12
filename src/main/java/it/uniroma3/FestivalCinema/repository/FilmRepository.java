package it.uniroma3.FestivalCinema.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.uniroma3.FestivalCinema.model.Film;

public interface FilmRepository extends JpaRepository<Film, Long> {

	// STRATEGIA 1 (baseline): nessun JOIN FETCH. Poiche' Film.regista e' EAGER
	// (Sezione 8.1), Hibernate carica comunque il regista di ogni film, ma con
	// una SELECT separata per ciascuno -> problema N+1, usato come confronto
	// nello script di Sezione 8.2.
	List<Film> findByFestivalId(Long festivalId);

	// Ricerca con filtri opzionali per l'elenco/ricerca film (Sezione 4.1, 9).
	// Il CAST esplicito su :titolo evita un errore di PostgreSQL ("function
	// lower(bytea) does not exist"): quando il parametro e' null (nessun filtro
	// sul titolo), il driver non riesce altrimenti a dedurne il tipo essendo
	// riusato sia nel controllo IS NULL sia dentro LOWER/CONCAT.
	@Query("SELECT f FROM Film f WHERE "
		+ "(:titolo IS NULL OR LOWER(f.titolo) LIKE LOWER(CONCAT('%', CAST(:titolo AS string), '%'))) AND "
		+ "(:genere IS NULL OR f.genere = :genere) AND "
		+ "(:anno IS NULL OR f.anno = :anno)")
	List<Film> cercaConFiltri(@Param("titolo") String titolo,
	                           @Param("genere") String genere,
	                           @Param("anno") Integer anno);

	// Film di un festival, con regista gia' caricato (Sezione 8.2: caso d'uso
	// "tutti i film di un festival con i relativi registi")
	@Query("SELECT DISTINCT f FROM Film f "
		+ "JOIN FETCH f.regista "
		+ "JOIN f.festival fe WHERE fe.id = :festivalId")
	List<Film> findByFestivalIdWithRegistaJoinFetch(@Param("festivalId") Long festivalId);

	@EntityGraph(attributePaths = { "regista" })
	@Query("SELECT f FROM Film f JOIN f.festival fe WHERE fe.id = :festivalId")
	List<Film> findByFestivalIdWithRegistaEntityGraph(@Param("festivalId") Long festivalId);

	@Query("SELECT DISTINCT f FROM Film f "
		+ "LEFT JOIN FETCH f.recensioni "
		+ "WHERE f.id = :id")
	Optional<Film> findByIdWithRecensioniJoinFetch(@Param("id") Long id);
}
