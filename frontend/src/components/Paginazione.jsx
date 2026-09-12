// Controlli di paginazione riusati da tutte le pagine che consumano un
// PaginaDTO (vedi backend it.uniroma3.FestivalCinema.dto.PaginaDTO).
export default function Paginazione({ pagina, onCambiaPagina }) {
	if (pagina.totalePagine <= 1) return null

	const numeri = Array.from({ length: pagina.totalePagine }, (_, i) => i)

	return (
		<nav className="paginazione">
			<button className="btn btn-outline" disabled={pagina.pagina === 0}
			        onClick={() => onCambiaPagina(pagina.pagina - 1)}>&laquo; Precedente</button>
			{numeri.map((i) => (
				<button key={i} className={`btn ${i === pagina.pagina ? '' : 'btn-outline'}`}
				        onClick={() => onCambiaPagina(i)}>{i + 1}</button>
			))}
			<button className="btn btn-outline" disabled={pagina.pagina >= pagina.totalePagine - 1}
			        onClick={() => onCambiaPagina(pagina.pagina + 1)}>Successiva &raquo;</button>
		</nav>
	)
}
