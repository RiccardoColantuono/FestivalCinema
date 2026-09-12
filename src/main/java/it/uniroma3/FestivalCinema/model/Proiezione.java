package it.uniroma3.FestivalCinema.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

@Entity
public class Proiezione {

	public enum Stato {
		SCHEDULED,
		COMPLETED,
		CANCELLED
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(nullable = false)
	private LocalDate data;

	@NotNull
	@Column(nullable = false)
	private LocalTime ora;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Stato stato;

	// Le tre associazioni ManyToOne sono EAGER (default JPA): una proiezione,
	// nel programma o nel dettaglio, viene sempre mostrata insieme a festival,
	// film e sala -> evita un giro di query aggiuntivo lato applicativo.
	// L'alternativa LAZY + join fetch mirato e' discussa/misurata nello script
	// di Sezione 8.2 per il caso d'uso "programma del festival".
	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "festival_id", nullable = false)
	private Festival festival;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "film_id", nullable = false)
	private Film film;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "sala_id", nullable = false)
	private Sala sala;

	public Proiezione() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getData() {
		return data;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}

	public LocalTime getOra() {
		return ora;
	}

	public void setOra(LocalTime ora) {
		this.ora = ora;
	}

	public Stato getStato() {
		return stato;
	}

	public void setStato(Stato stato) {
		this.stato = stato;
	}

	public Festival getFestival() {
		return festival;
	}

	public void setFestival(Festival festival) {
		this.festival = festival;
	}

	public Film getFilm() {
		return film;
	}

	public void setFilm(Film film) {
		this.film = film;
	}

	public Sala getSala() {
		return sala;
	}

	public void setSala(Sala sala) {
		this.sala = sala;
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
		Proiezione other = (Proiezione) obj;
		return Objects.equals(id, other.id);
	}
}
