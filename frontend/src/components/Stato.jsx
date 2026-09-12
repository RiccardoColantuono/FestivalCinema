// Componenti minimi e riusati da tutte le pagine per mostrare lo stato di
// caricamento/errore delle chiamate REST, evitando di ripetere la stessa
// logica in ogni pagina.
export function Caricamento() {
	return <p className="messaggio">Caricamento...</p>
}

export function Errore({ errore }) {
	if (!errore) return null
	return <p className="messaggio errore">{errore.message || 'Si e\' verificato un errore'}</p>
}
