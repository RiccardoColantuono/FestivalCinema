import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api.js'
import { Caricamento, Errore } from '../components/Stato.jsx'
import Paginazione from '../components/Paginazione.jsx'
import { emojiGenere } from '../categorie.js'

export default function FilmListPage() {
	const [filtri, setFiltri] = useState({ titolo: '', genere: '', anno: '', regista: '' })
	const [pagina, setPagina] = useState(null)
	const [errore, setErrore] = useState(null)

	function cerca(filtriAttuali, numeroPagina) {
		const query = new URLSearchParams({ page: numeroPagina })
		if (filtriAttuali.titolo) query.set('titolo', filtriAttuali.titolo)
		if (filtriAttuali.genere) query.set('genere', filtriAttuali.genere)
		if (filtriAttuali.anno) query.set('anno', filtriAttuali.anno)
		if (filtriAttuali.regista) query.set('regista', filtriAttuali.regista)
		api.get(`/api/movies?${query.toString()}`).then(setPagina).catch(setErrore)
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
				<h2>🎬 Film</h2>
			</div>

			{/* Ricerca per titolo, genere o regista (Sezione 9) */}
			<form className="search-form" onSubmit={handleSubmit}>
				<input placeholder="Cerca per titolo" value={filtri.titolo}
				       onChange={(e) => setFiltri({ ...filtri, titolo: e.target.value })} />
				<input placeholder="Genere" value={filtri.genere}
				       onChange={(e) => setFiltri({ ...filtri, genere: e.target.value })} />
				<input placeholder="Regista" value={filtri.regista}
				       onChange={(e) => setFiltri({ ...filtri, regista: e.target.value })} />
				<input placeholder="Anno" type="number" value={filtri.anno}
				       onChange={(e) => setFiltri({ ...filtri, anno: e.target.value })} />
				<button className="btn" type="submit">Cerca</button>
			</form>

			<Errore errore={errore} />
			{!pagina && !errore && <Caricamento />}
			{pagina && pagina.contenuto.length === 0 && <p className="messaggio">Nessun film trovato.</p>}
			<div className="grid">
				{pagina?.contenuto.map((f) => (
					<div className="card" key={f.id}>
						{f.locandina && (
							<img className="card-image" src={`http://localhost:8080/uploads/${f.locandina}`} alt="" />
						)}
						<h3>{f.titolo}</h3>
						<p className="meta">
							{f.anno}
							{f.genere && <span className="badge-genere" style={{ marginLeft: '0.5rem' }}>{emojiGenere(f.genere)} {f.genere}</span>}
						</p>
						<p className="meta">{f.regista ? `${f.regista.nome} ${f.regista.cognome}` : ''}</p>
						<Link className="btn" to={`/film/${f.id}`}>Dettagli</Link>
					</div>
				))}
			</div>

			{pagina && <Paginazione pagina={pagina} onCambiaPagina={(p) => cerca(filtri, p)} />}
		</div>
	)
}
