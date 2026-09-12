package it.uniroma3.FestivalCinema.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.uniroma3.FestivalCinema.model.Proiezione;

public interface ProiezioneRepository extends JpaRepository<Proiezione, Long> {

	// Verifica di consistenza richiesta in Sezione 4.3: non deve essere possibile
	// programmare due proiezioni nella stessa sala nello stesso intervallo temporale.
	// Confronto semplificato basato su data + ora esatta; il Service la usa dentro
	// una singola transazione insieme alla creazione, per evitare race condition.
	@Query("SELECT p FROM Proiezione p WHERE p.sala.id = :salaId AND p.data = :data AND p.ora = :ora "
		+ "AND p.stato <> it.uniroma3.FestivalCinema.model.Proiezione.Stato.CANCELLED "
		+ "AND (:escludiId IS NULL OR p.id <> :escludiId)")
	List<Proiezione> findConflittiSala(@Param("salaId") Long salaId,
	                                    @Param("data") LocalDate data,
	                                    @Param("ora") LocalTime ora,
	                                    @Param("escludiId") Long escludiId);

	// Programma di un festival ordinato, film e sala gia' caricati (EAGER, vedi Film/Sala su Proiezione)
	List<Proiezione> findByFestivalIdOrderByDataAscOraAsc(Long festivalId);

	@Query("SELECT DISTINCT p FROM Proiezione p "
		+ "JOIN FETCH p.film "
		+ "JOIN FETCH p.sala "
		+ "WHERE p.festival.id = :festivalId ORDER BY p.data, p.ora")
	List<Proiezione> findByFestivalIdWithFilmESalaJoinFetch(@Param("festivalId") Long festivalId);

	Optional<Proiezione> findById(Long id);
}
