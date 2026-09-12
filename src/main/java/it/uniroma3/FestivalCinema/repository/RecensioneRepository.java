package it.uniroma3.FestivalCinema.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import it.uniroma3.FestivalCinema.model.Recensione;

public interface RecensioneRepository extends JpaRepository<Recensione, Long> {

	List<Recensione> findByFilmId(Long filmId);

	// Usata dal Service per impedire piu' di una recensione dello stesso utente
	// sullo stesso film (Sezione 3, 4.2), oltre al vincolo UNIQUE a livello DB.
	Optional<Recensione> findByFilmIdAndAutoreUsername(Long filmId, String autoreUsername);
}
