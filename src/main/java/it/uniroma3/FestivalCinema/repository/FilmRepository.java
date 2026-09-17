package it.uniroma3.FestivalCinema.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	// Ricerca con filtri opzionali (titolo, genere, regista) ed elenco impaginato
	// per l'elenco/ricerca film (Sezione 4.1, 9). Il CAST esplicito sui parametri
	// stringa evita un errore di PostgreSQL ("function lower(bytea) does not
	// exist"): quando un parametro e' null (filtro non impostato), il driver non
	// riesce altrimenti a dedurne il tipo essendo riusato sia nel controllo IS
	// NULL sia dentro LOWER/CONCAT. Il countQuery e' esplicito (anziche' derivato
	// automaticamente da Spring Data) perche' la clausola su regista attraversa
	// un'associazione (f.regista.nome/cognome).
	@Query(value = "SELECT f FROM Film f WHERE "
		+ "(:titolo IS NULL OR LOWER(f.titolo) LIKE LOWER(CONCAT('%', CAST(:titolo AS string), '%'))) AND "
		+ "(:genere IS NULL OR LOWER(f.genere) LIKE LOWER(CONCAT('%', CAST(:genere AS string), '%'))) AND "
		+ "(:anno IS NULL OR f.anno = :anno) AND "
		+ "(:regista IS NULL OR ("
		+ "  LOWER(f.regista.nome) LIKE LOWER(CONCAT('%', CAST(:regista AS string), '%')) OR "
		+ "  LOWER(f.regista.cognome) LIKE LOWER(CONCAT('%', CAST(:regista AS string), '%'))"
		+ "))",
		countQuery = "SELECT COUNT(f) FROM Film f WHERE "
		+ "(:titolo IS NULL OR LOWER(f.titolo) LIKE LOWER(CONCAT('%', CAST(:titolo AS string), '%'))) AND "
		+ "(:genere IS NULL OR LOWER(f.genere) LIKE LOWER(CONCAT('%', CAST(:genere AS string), '%'))) AND "
		+ "(:anno IS NULL OR f.anno = :anno) AND "
		+ "(:regista IS NULL OR ("
		+ "  LOWER(f.regista.nome) LIKE LOWER(CONCAT('%', CAST(:regista AS string), '%')) OR "
		+ "  LOWER(f.regista.cognome) LIKE LOWER(CONCAT('%', CAST(:regista AS string), '%'))"
		+ "))")
	Page<Film> cercaConFiltri(@Param("titolo") String titolo,
	                           @Param("genere") String genere,
	                           @Param("anno") Integer anno,
	                           @Param("regista") String regista,
	                           Pageable pageable);

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
