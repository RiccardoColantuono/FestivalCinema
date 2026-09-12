package it.uniroma3.FestivalCinema.dto;

import it.uniroma3.FestivalCinema.model.Festival;

public class FestivalDTO {
	private Long id;
	private String nome;
	private Integer anno;
	private String citta;
	private String dataInizio;
	private String dataFine;
	private String descrizione;

	public FestivalDTO(Long id, String nome, Integer anno, String citta, String dataInizio,
	                    String dataFine, String descrizione) {
		this.id = id;
		this.nome = nome;
		this.anno = anno;
		this.citta = citta;
		this.dataInizio = dataInizio;
		this.dataFine = dataFine;
		this.descrizione = descrizione;
	}

	public static FestivalDTO from(Festival f) {
		return new FestivalDTO(f.getId(), f.getNome(), f.getAnno(), f.getCitta(),
			f.getDataInizio() != null ? f.getDataInizio().toString() : null,
			f.getDataFine() != null ? f.getDataFine().toString() : null,
			f.getDescrizione());
	}

	public Long getId() { return id; }
	public String getNome() { return nome; }
	public Integer getAnno() { return anno; }
	public String getCitta() { return citta; }
	public String getDataInizio() { return dataInizio; }
	public String getDataFine() { return dataFine; }
	public String getDescrizione() { return descrizione; }
}
