import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api.js'
import { Caricamento, Errore } from '../components/Stato.jsx'
import Paginazione from '../components/Paginazione.jsx'
import { statoProiezione } from '../categorie.js'

// Ricerca delle proiezioni per data (Sezione 9).
export default function ProiezioniPage() {
	const [data, setData] = useState('')
	const [pagina, setPagina] = useState(null)
	const [errore, setErrore] = useState(null)

	function cerca(dataAttuale, numeroPagina) {
		const query = new URLSearchParams({ page: numeroPagina })
		if (dataAttuale) query.set('data', dataAttuale)
		api.get(`/api/proiezioni?${query.toString()}`).then(setPagina).catch(setErrore)
	}

	useEffect(() => {
		cerca(data, 0)
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [])

	function handleSubmit(e) {
		e.preventDefault()
		cerca(data, 0)
	}

	return (
		<div className="container">
			<div className="page-header">
				<h2>🕒 Proiezioni</h2>
			</div>

			<form className="search-form" onSubmit={handleSubmit}>
				<input type="date" value={data} onChange={(e) => setData(e.target.value)} />
				<button className="btn" type="submit">Cerca</button>
				{data && (
					<button type="button" className="btn btn-outline" onClick={() => { setData(''); cerca('', 0) }}>
						Azzera
					</button>
				)}
			</form>

			<Errore errore={errore} />
			{!pagina && !errore && <Caricamento />}
			{pagina && pagina.contenuto.length === 0 && <p className="messaggio">Nessuna proiezione trovata.</p>}
			{pagina && pagina.contenuto.length > 0 && (
				<table>
					<thead>
						<tr><th>Data</th><th>Ora</th><th>Festival</th><th>Film</th><th>Sala</th><th>Stato</th></tr>
					</thead>
					<tbody>
						{pagina.contenuto.map((p) => (
							<tr key={p.id}>
								<td>{p.data}</td>
								<td>{p.ora}</td>
								<td>{p.festival ? <Link to={`/festival/${p.festival.id}`}>{p.festival.nome}</Link> : ''}</td>
								<td>{p.film ? <Link to={`/film/${p.film.id}`}>{p.film.titolo}</Link> : ''}</td>
								<td>{p.sala?.nome}</td>
								<td>
									<span className={`badge-stato ${statoProiezione(p.stato).classe}`}>
										{statoProiezione(p.stato).emoji} {statoProiezione(p.stato).etichetta}
									</span>
								</td>
							</tr>
						))}
					</tbody>
				</table>
			)}

			{pagina && <Paginazione pagina={pagina} onCambiaPagina={(p) => cerca(data, p)} />}
		</div>
	)
}
