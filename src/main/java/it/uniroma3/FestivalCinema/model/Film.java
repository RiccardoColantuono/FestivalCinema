package it.uniroma3.FestivalCinema.model;

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
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
public class Film {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Column(nullable = false)
	private String titolo;

	@NotNull
	@Column(nullable = false)
	private Integer anno;

	// durata in minuti
	@Positive
	@Column(nullable = false)
	private int durata;

	private String genere;

	private String paeseProduzione;

	// ManyToOne: fetch EAGER di default in JPA, lo rendiamo esplicito.
	// Il regista e' quasi sempre necessario insieme al film (es. nel dettaglio
	// film, Sezione 4.1), quindi EAGER e' motivato qui.
	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "regista_id", nullable = false)
	private Regista regista;

	// ManyToMany: un film puo' partecipare a piu' festival e un festival presenta
	// piu' film (Sezione 3). LAZY perche' la lista completa dei festival non serve
	// in ogni contesto in cui si carica un film (es. lista film di un festival).
	@ManyToMany(mappedBy = "film", fetch = FetchType.LAZY)
	private List<Festival> festival = new ArrayList<>();

	// OneToMany: LAZY di default, corretto qui perche' le proiezioni di un film
	// vengono richieste solo nel dettaglio del film, non nelle liste.
	@OneToMany(mappedBy = "film")
	private List<Proiezione> proiezioni = new ArrayList<>();

	@OneToMany(mappedBy = "film")
	private List<Recensione> recensioni = new ArrayList<>();

	public Film() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public Integer getAnno() {
		return anno;
	}

	public void setAnno(Integer anno) {
		this.anno = anno;
	}

	public int getDurata() {
		return durata;
	}

	public void setDurata(int durata) {
		this.durata = durata;
	}

	public String getGenere() {
		return genere;
	}

	public void setGenere(String genere) {
		this.genere = genere;
	}

	public String getPaeseProduzione() {
		return paeseProduzione;
	}

	public void setPaeseProduzione(String paeseProduzione) {
		this.paeseProduzione = paeseProduzione;
	}

	public Regista getRegista() {
		return regista;
	}

	public void setRegista(Regista regista) {
		this.regista = regista;
	}

	public List<Festival> getFestival() {
		return festival;
	}

	public void setFestival(List<Festival> festival) {
		this.festival = festival;
	}

	public List<Proiezione> getProiezioni() {
		return proiezioni;
	}

	public void setProiezioni(List<Proiezione> proiezioni) {
		this.proiezioni = proiezioni;
	}

	public List<Recensione> getRecensioni() {
		return recensioni;
	}

	public void setRecensioni(List<Recensione> recensioni) {
		this.recensioni = recensioni;
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
		Film other = (Film) obj;
		return Objects.equals(id, other.id);
	}
}
