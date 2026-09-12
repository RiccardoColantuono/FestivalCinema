package it.uniroma3.FestivalCinema.dto;

import it.uniroma3.FestivalCinema.model.Sala;

public class SalaDTO {
	private Long id;
	private String nome;
	private int capienza;

	public SalaDTO(Long id, String nome, int capienza) {
		this.id = id;
		this.nome = nome;
		this.capienza = capienza;
	}

	public static SalaDTO from(Sala s) {
		return new SalaDTO(s.getId(), s.getNome(), s.getCapienza());
	}

	public Long getId() { return id; }
	public String getNome() { return nome; }
	public int getCapienza() { return capienza; }
}
