package it.uniroma3.FestivalCinema.dto;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;

// Wrapper generico per esporre un Page<T> di Spring Data via REST (Sezione 9/10):
// evita di serializzare direttamente l'oggetto Page di Spring, che espone molti
// campi interni non necessari al frontend.
public class PaginaDTO<T> {

	private final List<T> contenuto;
	private final int pagina;
	private final int totalePagine;
	private final long totaleElementi;

	public PaginaDTO(List<T> contenuto, int pagina, int totalePagine, long totaleElementi) {
		this.contenuto = contenuto;
		this.pagina = pagina;
		this.totalePagine = totalePagine;
		this.totaleElementi = totaleElementi;
	}

	public static <E, D> PaginaDTO<D> from(Page<E> page, Function<E, D> mappatura) {
		return new PaginaDTO<>(page.getContent().stream().map(mappatura).toList(),
			page.getNumber(), page.getTotalPages(), page.getTotalElements());
	}

	public List<T> getContenuto() {
		return contenuto;
	}

	public int getPagina() {
		return pagina;
	}

	public int getTotalePagine() {
		return totalePagine;
	}

	public long getTotaleElementi() {
		return totaleElementi;
	}
}
