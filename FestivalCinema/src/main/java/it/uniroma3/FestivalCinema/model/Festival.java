package it.uniroma3.FestivalCinema.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Festival {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Column(nullable = false)
	private String nome;

	@NotNull
	@Column(nullable = false)
	private Integer anno;

	private String citta;

	@NotNull
	@Column(nullable = false)
	private LocalDate dataInizio;

	@NotNull
	@Column(nullable = false)
	private LocalDate dataFine;

	@Column(length = 2000)
	private String descrizione;

	// Lato proprietario della relazione molti-a-molti (Sezione 3 e 11).
	// LAZY: la lista dei film non serve quando si mostra solo l'elenco dei festival
	// (Sezione 4.1), ma solo nel dettaglio -> evitiamo un caricamento inutile.
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
		name = "festival_film",
		joinColumns = @JoinColumn(name = "festival_id"),
		inverseJoinColumns = @JoinColumn(name = "film_id")
	)
	private List<Film> film = new ArrayList<>();

	// Un festival prevede piu' proiezioni (Sezione 3)
	@OneToMany(mappedBy = "festival")
	private List<Proiezione> proiezioni = new ArrayList<>();

	public Festival() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getAnno() {
		return anno;
	}

	public void setAnno(Integer anno) {
		this.anno = anno;
	}

	public String getCitta() {
		return citta;
	}

	public void setCitta(String citta) {
		this.citta = citta;
	}

	public LocalDate getDataInizio() {
		return dataInizio;
	}

	public void setDataInizio(LocalDate dataInizio) {
		this.dataInizio = dataInizio;
	}

	public LocalDate getDataFine() {
		return dataFine;
	}

	public void setDataFine(LocalDate dataFine) {
		this.dataFine = dataFine;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public List<Film> getFilm() {
		return film;
	}

	public void setFilm(List<Film> film) {
		this.film = film;
	}

	public List<Proiezione> getProiezioni() {
		return proiezioni;
	}

	public void setProiezioni(List<Proiezione> proiezioni) {
		this.proiezioni = proiezioni;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Festival other = (Festival) obj;
		return Objects.equals(id, other.id);
	}
}
