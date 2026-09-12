import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { api } from '../api.js'
import { Caricamento, Errore } from '../components/Stato.jsx'

export default function FestivalDetailPage() {
	const { id } = useParams()
	const [festival, setFestival] = useState(null)
	const [film, setFilm] = useState(null)
	const [proiezioni, setProiezioni] = useState(null)
	const [errore, setErrore] = useState(null)

	useEffect(() => {
		setFestival(null)
		setFilm(null)
		setProiezioni(null)
		Promise.all([
			api.get(`/api/festival/${id}`),
			api.get(`/api/festival/${id}/movies`),
			api.get(`/api/festival/${id}/screenings`),
		])
			.then(([f, m, p]) => {
				setFestival(f)
				setFilm(m)
				setProiezioni(p)
			})
			.catch(setErrore)
	}, [id])

	if (errore) return <div className="container"><Errore errore={errore} /></div>
	if (!festival) return <div className="container"><Caricamento /></div>

	return (
		<div className="container">
			<h1>{festival.nome}</h1>
			<p className="meta">{festival.citta} - {festival.dataInizio} &rarr; {festival.dataFine}</p>
			<p>{festival.descrizione}</p>

			<h3>Film in programma</h3>
			{film.length === 0 && <p className="messaggio">Nessun film ancora associato.</p>}
			<div className="grid">
				{film.map((f) => (
					<div className="card" key={f.id}>
						<h3>{f.titolo}</h3>
						<p className="meta">{f.anno} &bull; {f.genere} &bull; {f.durata} min</p>
						<Link className="btn btn-outline" to={`/film/${f.id}`}>Dettagli film</Link>
					</div>
				))}
			</div>

			<h3 style={{ marginTop: '2rem' }}>Programma delle proiezioni</h3>
			{proiezioni.length === 0 && <p className="messaggio">Nessuna proiezione ancora programmata.</p>}
			{proiezioni.length > 0 && (
				<table>
					<thead>
						<tr><th>Data</th><th>Ora</th><th>Film</th><th>Sala</th><th>Stato</th></tr>
					</thead>
					<tbody>
						{proiezioni.map((p) => (
							<tr key={p.id}>
								<td>{p.data}</td>
								<td>{p.ora}</td>
								<td>{p.film?.titolo}</td>
								<td>{p.sala?.nome}</td>
								<td>{p.stato}</td>
							</tr>
						))}
					</tbody>
				</table>
			)}
		</div>
	)
}
