import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api.js'
import { Caricamento, Errore } from '../components/Stato.jsx'
import Paginazione from '../components/Paginazione.jsx'

export default function FestivalListPage() {
	const [filtri, setFiltri] = useState({ nome: '', anno: '', citta: '' })
	const [pagina, setPagina] = useState(null)
	const [errore, setErrore] = useState(null)

	function cerca(filtriAttuali, numeroPagina) {
		const query = new URLSearchParams({ page: numeroPagina })
		if (filtriAttuali.nome) query.set('nome', filtriAttuali.nome)
		if (filtriAttuali.anno) query.set('anno', filtriAttuali.anno)
		if (filtriAttuali.citta) query.set('citta', filtriAttuali.citta)
		api.get(`/api/festival?${query.toString()}`).then(setPagina).catch(setErrore)
	}

	useEffect(() => {
		cerca(filtri, 0)
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [])

	function handleSubmit(e) {
		e.preventDefault()
		cerca(filtri, 0)
	}

	return (
		<div className="container">
			<div className="page-header">
				<h2>🎪 Festival cinematografici</h2>
			</div>

			<form className="search-form" onSubmit={handleSubmit}>
				<input placeholder="Cerca per nome" value={filtri.nome}
				       onChange={(e) => setFiltri({ ...filtri, nome: e.target.value })} />
				<input placeholder="Citta'" value={filtri.citta}
				       onChange={(e) => setFiltri({ ...filtri, citta: e.target.value })} />
				<input placeholder="Anno" type="number" value={filtri.anno}
				       onChange={(e) => setFiltri({ ...filtri, anno: e.target.value })} />
				<button className="btn" type="submit">Cerca</button>
			</form>

			<Errore errore={errore} />
			{!pagina && !errore && <Caricamento />}
			{pagina && pagina.contenuto.length === 0 && <p className="messaggio">Nessun festival disponibile.</p>}
			<div className="grid">
				{pagina?.contenuto.map((f) => (
					<div className="card" key={f.id}>
						{f.immagineCopertina && (
							<img className="card-image" src={`http://localhost:8080/uploads/${f.immagineCopertina}`} alt="" />
						)}
						<h3>{f.nome}</h3>
						<p className="meta">{f.citta} - {f.anno}</p>
						<p className="meta">{f.dataInizio} &rarr; {f.dataFine}</p>
						<Link className="btn" to={`/festival/${f.id}`}>Dettagli</Link>
					</div>
				))}
			</div>

			{pagina && <Paginazione pagina={pagina} onCambiaPagina={(p) => cerca(filtri, p)} />}
		</div>
	)
}
