package it.uniroma3.FestivalCinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.uniroma3.FestivalCinema.model.Regista;

public interface RegistaRepository extends JpaRepository<Regista, Long> {
}
