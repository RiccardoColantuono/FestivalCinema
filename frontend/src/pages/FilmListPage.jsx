import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api.js'
import { Caricamento, Errore } from '../components/Stato.jsx'

export default function FilmListPage() {
	const [filtri, setFiltri] = useState({ titolo: '', genere: '', anno: '' })
	const [film, setFilm] = useState(null)
	const [errore, setErrore] = useState(null)

	function cerca(filtriAttuali) {
		const query = new URLSearchParams()
		if (filtriAttuali.titolo) query.set('titolo', filtriAttuali.titolo)
		if (filtriAttuali.genere) query.set('genere', filtriAttuali.genere)
		if (filtriAttuali.anno) query.set('anno', filtriAttuali.anno)
		api.get(`/api/movies?${query.toString()}`).then(setFilm).catch(setErrore)
	}

	useEffect(() => {
		cerca(filtri)
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [])

	function handleSubmit(e) {
		e.preventDefault()
		cerca(filtri)
	}

	return (
		<div className="container">
			<div className="page-header">
				<h2>Film</h2>
			</div>

			<form className="search-form" onSubmit={handleSubmit}>
				<input placeholder="Cerca per titolo" value={filtri.titolo}
				       onChange={(e) => setFiltri({ ...filtri, titolo: e.target.value })} />
				<input placeholder="Genere" value={filtri.genere}
				       onChange={(e) => setFiltri({ ...filtri, genere: e.target.value })} />
				<input placeholder="Anno" type="number" value={filtri.anno}
				       onChange={(e) => setFiltri({ ...filtri, anno: e.target.value })} />
				<button className="btn" type="submit">Cerca</button>
			</form>

			<Errore errore={errore} />
			{!film && !errore && <Caricamento />}
			{film && film.length === 0 && <p className="messaggio">Nessun film trovato.</p>}
			<div className="grid">
				{film?.map((f) => (
					<div className="card" key={f.id}>
						<h3>{f.titolo}</h3>
						<p className="meta">{f.anno} &bull; {f.genere}</p>
						<p className="meta">{f.regista ? `${f.regista.nome} ${f.regista.cognome}` : ''}</p>
						<Link className="btn" to={`/film/${f.id}`}>Dettagli</Link>
					</div>
				))}
			</div>
		</div>
	)
}
