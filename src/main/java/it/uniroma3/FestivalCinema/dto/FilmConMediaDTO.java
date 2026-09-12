package it.uniroma3.FestivalCinema.dto;

import it.uniroma3.FestivalCinema.model.Film;

// Una riga della classifica film per voto medio (statistiche generali sulle
// recensioni, vedi RecensioneRepository.classificaFilmPerVotoMedio).
public class FilmConMediaDTO {

	private final FilmDTO film;
	private final double votoMedio;
	private final long numeroRecensioni;

	public FilmConMediaDTO(Film film, double votoMedio, long numeroRecensioni) {
		this.film = FilmDTO.from(film);
		this.votoMedio = votoMedio;
		this.numeroRecensioni = numeroRecensioni;
	}

	public FilmDTO getFilm() {
		return film;
	}

	public double getVotoMedio() {
		return votoMedio;
	}

	public long getNumeroRecensioni() {
		return numeroRecensioni;
	}
}
