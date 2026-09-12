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
	// Necessario per la ricerca proiezioni cross-festival (GET /api/proiezioni):
	// nel programma di un singolo festival questo campo e' ridondante (il
	// festival e' gia' noto dal contesto della pagina), ma li' non da' fastidio.
	private FestivalDTO festival;

	public ProiezioneDTO(Long id, String data, String ora, String stato, FilmDTO film, SalaDTO sala,
	                      FestivalDTO festival) {
		this.id = id;
		this.data = data;
		this.ora = ora;
		this.stato = stato;
		this.film = film;
		this.sala = sala;
		this.festival = festival;
	}

	public static ProiezioneDTO from(Proiezione p) {
		return new ProiezioneDTO(
			p.getId(),
			p.getData() != null ? p.getData().toString() : null,
			p.getOra() != null ? p.getOra().toString() : null,
			p.getStato() != null ? p.getStato().name() : null,
			p.getFilm() != null ? FilmDTO.from(p.getFilm()) : null,
			p.getSala() != null ? SalaDTO.from(p.getSala()) : null,
			p.getFestival() != null ? FestivalDTO.from(p.getFestival()) : null
		);
	}

	public Long getId() { return id; }
	public String getData() { return data; }
	public String getOra() { return ora; }
	public String getStato() { return stato; }
	public FilmDTO getFilm() { return film; }
	public SalaDTO getSala() { return sala; }
	public FestivalDTO getFestival() { return festival; }
}
