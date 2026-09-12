package it.uniroma3.FestivalCinema.dto;

import java.util.Map;

// Statistiche sulle recensioni di un film: numero totale, voto medio (null se
// nessuna recensione) e distribuzione dei voti (chiavi 1..5 sempre presenti,
// anche a zero, cosi' il grafico a barre nel template non deve gestire buchi).
public class StatisticheRecensioniDTO {

	private final long numeroRecensioni;
	private final Double votoMedio;
	private final Map<Integer, Long> distribuzioneVoti;

	public StatisticheRecensioniDTO(long numeroRecensioni, Double votoMedio, Map<Integer, Long> distribuzioneVoti) {
		this.numeroRecensioni = numeroRecensioni;
		this.votoMedio = votoMedio;
		this.distribuzioneVoti = distribuzioneVoti;
	}

	public long getNumeroRecensioni() {
		return numeroRecensioni;
	}

	public Double getVotoMedio() {
		return votoMedio;
	}

	public Map<Integer, Long> getDistribuzioneVoti() {
		return distribuzioneVoti;
	}
}
