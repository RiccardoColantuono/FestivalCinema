package it.uniroma3.FestivalCinema.dto;

import it.uniroma3.FestivalCinema.model.Proiezione;

// DTO usato dal componente React "programma delle proiezioni" (Sezione 9)
public class ProiezioneDTO {
	private Long id;
	private String data;
	private String ora;
	private String stato;
	private FilmDTO film;
	private SalaDTO sala;

	public ProiezioneDTO(Long id, String data, String ora, String stato, FilmDTO film, SalaDTO sala) {
		this.id = id;
		this.data = data;
		this.ora = ora;
		this.stato = stato;
		this.film = film;
		this.sala = sala;
	}

	public static ProiezioneDTO from(Proiezione p) {
		return new ProiezioneDTO(
			p.getId(),
			p.getData() != null ? p.getData().toString() : null,
			p.getOra() != null ? p.getOra().toString() : null,
			p.getStato() != null ? p.getStato().name() : null,
			p.getFilm() != null ? FilmDTO.from(p.getFilm()) : null,
			p.getSala() != null ? SalaDTO.from(p.getSala()) : null
		);
	}

	public Long getId() { return id; }
	public String getData() { return data; }
	public String getOra() { return ora; }
	public String getStato() { return stato; }
	public FilmDTO getFilm() { return film; }
	public SalaDTO getSala() { return sala; }
}
