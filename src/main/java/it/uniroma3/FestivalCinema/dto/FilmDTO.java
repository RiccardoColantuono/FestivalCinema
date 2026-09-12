package it.uniroma3.FestivalCinema.dto;

import it.uniroma3.FestivalCinema.model.Film;

public class FilmDTO {
	private Long id;
	private String titolo;
	private Integer anno;
	private int durata;
	private String genere;
	private String paeseProduzione;
	private String locandina;
	private RegistaDTO regista;

	public FilmDTO(Long id, String titolo, Integer anno, int durata, String genere,
	                String paeseProduzione, String locandina, RegistaDTO regista) {
		this.id = id;
		this.titolo = titolo;
		this.anno = anno;
		this.durata = durata;
		this.genere = genere;
		this.paeseProduzione = paeseProduzione;
		this.locandina = locandina;
		this.regista = regista;
	}

	public static FilmDTO from(Film f) {
		return new FilmDTO(f.getId(), f.getTitolo(), f.getAnno(), f.getDurata(), f.getGenere(),
			f.getPaeseProduzione(), f.getLocandina(),
			f.getRegista() != null ? RegistaDTO.from(f.getRegista()) : null);
	}

	public Long getId() { return id; }
	public String getTitolo() { return titolo; }
	public Integer getAnno() { return anno; }
	public int getDurata() { return durata; }
	public String getGenere() { return genere; }
	public String getPaeseProduzione() { return paeseProduzione; }
	// Nome del file: il client ricostruisce l'URL come /uploads/<locandina>.
	public String getLocandina() { return locandina; }
	public RegistaDTO getRegista() { return regista; }
}
