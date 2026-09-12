package it.uniroma3.FestivalCinema.dto;

import it.uniroma3.FestivalCinema.model.Regista;

public class RegistaDTO {
	private Long id;
	private String nome;
	private String cognome;
	private String nazionalita;

	public RegistaDTO(Long id, String nome, String cognome, String nazionalita) {
		this.id = id;
		this.nome = nome;
		this.cognome = cognome;
		this.nazionalita = nazionalita;
	}

	public static RegistaDTO from(Regista r) {
		return new RegistaDTO(r.getId(), r.getNome(), r.getCognome(), r.getNazionalita());
	}

	public Long getId() { return id; }
	public String getNome() { return nome; }
	public String getCognome() { return cognome; }
	public String getNazionalita() { return nazionalita; }
}
