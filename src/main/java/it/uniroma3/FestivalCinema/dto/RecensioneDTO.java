package it.uniroma3.FestivalCinema.dto;

import it.uniroma3.FestivalCinema.model.Recensione;

public class RecensioneDTO {
	private Long id;
	private String testo;
	private Integer voto;
	private String data;
	private String autoreUsername;

	public RecensioneDTO(Long id, String testo, Integer voto, String data, String autoreUsername) {
		this.id = id;
		this.testo = testo;
		this.voto = voto;
		this.data = data;
		this.autoreUsername = autoreUsername;
	}

	public static RecensioneDTO from(Recensione r) {
		return new RecensioneDTO(r.getId(), r.getTesto(), r.getVoto(),
			r.getData() != null ? r.getData().toString() : null,
			r.getAutore() != null ? r.getAutore().getUsername() : null);
	}

	public Long getId() { return id; }
	public String getTesto() { return testo; }
	public Integer getVoto() { return voto; }
	public String getData() { return data; }
	public String getAutoreUsername() { return autoreUsername; }
}
