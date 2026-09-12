package it.uniroma3.FestivalCinema.model;

import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// Vincolo di unicita' a livello DB, oltre al controllo applicativo nel Service:
// un utente puo' inserire al massimo una recensione per uno stesso film (Sezione 3, 4.2)
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "film_id", "autore_id" }))
public class Recensione {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Column(length = 2000, nullable = false)
	private String testo;

	@NotNull
	@Min(1)
	@Max(5)
	@Column(nullable = false)
	private Integer voto;

	@NotNull
	@Column(nullable = false)
	private LocalDate data;

	// EAGER: una recensione mostrata da sola (es. lista recensioni di un film)
	// e' incompleta senza sapere di quale film si tratta e chi l'ha scritta;
	// il numero di recensioni per pagina e' comunque limitato (no problema N+1 rilevante).
	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "film_id", nullable = false)
	private Film film;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "autore_id", nullable = false)
	private Utente autore;

	public Recensione() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTesto() {
		return testo;
	}

	public void setTesto(String testo) {
		this.testo = testo;
	}

	public Integer getVoto() {
		return voto;
	}

	public void setVoto(Integer voto) {
		this.voto = voto;
	}

	public LocalDate getData() {
		return data;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}

	public Film getFilm() {
		return film;
	}

	public void setFilm(Film film) {
		this.film = film;
	}

	public Utente getAutore() {
		return autore;
	}

	public void setAutore(Utente autore) {
		this.autore = autore;
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
		Recensione other = (Recensione) obj;
		return Objects.equals(id, other.id);
	}
}
