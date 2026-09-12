package it.uniroma3.FestivalCinema.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.uniroma3.FestivalCinema.model.Festival;

public interface FestivalRepository extends JpaRepository<Festival, Long> {

	// Ricerca con filtri opzionali (nome, anno, citta') ed elenco impaginato per
	// l'elenco/ricerca festival (Sezione 4.1, 9). Vedi FilmRepository.cercaConFiltri
	// per la spiegazione del CAST esplicito sui parametri stringa.
	@Query(value = "SELECT f FROM Festival f WHERE "
		+ "(:nome IS NULL OR LOWER(f.nome) LIKE LOWER(CONCAT('%', CAST(:nome AS string), '%'))) AND "
		+ "(:anno IS NULL OR f.anno = :anno) AND "
		+ "(:citta IS NULL OR LOWER(f.citta) LIKE LOWER(CONCAT('%', CAST(:citta AS string), '%')))",
		countQuery = "SELECT COUNT(f) FROM Festival f WHERE "
		+ "(:nome IS NULL OR LOWER(f.nome) LIKE LOWER(CONCAT('%', CAST(:nome AS string), '%'))) AND "
		+ "(:anno IS NULL OR f.anno = :anno) AND "
		+ "(:citta IS NULL OR LOWER(f.citta) LIKE LOWER(CONCAT('%', CAST(:citta AS string), '%')))")
	Page<Festival> cercaConFiltri(@Param("nome") String nome,
	                               @Param("anno") Integer anno,
	                               @Param("citta") String citta,
	                               Pageable pageable);

	// STRATEGIA 1 (baseline): findById standard, i film restano LAZY.
	// Se poi si itera festival.getFilm() per N festival -> problema N+1 (Sezione 8.2).

	// STRATEGIA 2: JOIN FETCH esplicito, un'unica query SQL per festival + film.
	@Query("SELECT DISTINCT f FROM Festival f LEFT JOIN FETCH f.film WHERE f.id = :id")
	Optional<Festival> findByIdWithFilmJoinFetch(Long id);

	@Query("SELECT DISTINCT f FROM Festival f LEFT JOIN FETCH f.film")
	List<Festival> findAllWithFilmJoinFetch();

	// STRATEGIA 3: EntityGraph dichiarativo, equivalente funzionale al JOIN FETCH
	// ma espresso senza scrivere JPQL a mano.
	@EntityGraph(attributePaths = { "film" })
	@Query("SELECT f FROM Festival f")
	List<Festival> findAllWithFilmEntityGraph();

	// Programma del festival: proiezioni + film + sala in un'unica query,
	// usato per popolare la pagina "programma delle proiezioni" (Sezione 4.1)
	@Query("SELECT DISTINCT f FROM Festival f "
		+ "LEFT JOIN FETCH f.proiezioni p "
		+ "LEFT JOIN FETCH p.film "
		+ "LEFT JOIN FETCH p.sala "
		+ "WHERE f.id = :id")
	Optional<Festival> findByIdWithProgrammaJoinFetch(Long id);
}
