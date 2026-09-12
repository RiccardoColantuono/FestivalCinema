package it.uniroma3.FestivalCinema.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.uniroma3.FestivalCinema.model.Film;

public interface FilmRepository extends JpaRepository<Film, Long> {

	// Ricerca con filtri opzionali per l'elenco/ricerca film (Sezione 4.1, 9)
	@Query("SELECT f FROM Film f WHERE "
		+ "(:titolo IS NULL OR LOWER(f.titolo) LIKE LOWER(CONCAT('%', :titolo, '%'))) AND "
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
