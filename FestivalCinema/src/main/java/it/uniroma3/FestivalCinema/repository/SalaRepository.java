package it.uniroma3.FestivalCinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.uniroma3.FestivalCinema.model.Sala;

public interface SalaRepository extends JpaRepository<Sala, Long> {
}
