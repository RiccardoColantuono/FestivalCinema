import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api.js'
import { Caricamento, Errore } from '../components/Stato.jsx'
import { MEDAGLIE } from '../categorie.js'

// Statistiche generali sulle recensioni: classifica dei film per voto medio.
export default function StatistichePage() {
	const [classifica, setClassifica] = useState(null)
	const [errore, setErrore] = useState(null)

	useEffect(() => {
		api.get('/api/statistiche/classifica').then(setClassifica).catch(setErrore)
	}, [])

	return (
		<div className="container">
			<div className="page-header">
				<h2>📊 Statistiche sulle recensioni</h2>
			</div>
			<p className="messaggio">Classifica dei film per voto medio (solo film con almeno una recensione).</p>

			<Errore errore={errore} />
			{!classifica && !errore && <Caricamento />}
			{classifica && classifica.length === 0 && <p className="messaggio">Nessuna recensione ancora inserita.</p>}
			{classifica && classifica.length > 0 && (
				<table>
					<thead>
						<tr><th>#</th><th>Film</th><th>Voto medio</th><th>Recensioni</th></tr>
					</thead>
					<tbody>
						{classifica.map((riga, indice) => (
							<tr key={riga.film.id}>
								<td>{MEDAGLIE[indice] || indice + 1}</td>
								<td><Link to={`/film/${riga.film.id}`}>{riga.film.titolo}</Link></td>
								<td>{riga.votoMedio.toFixed(1)}/5</td>
								<td>{riga.numeroRecensioni}</td>
							</tr>
						))}
					</tbody>
				</table>
			)}
		</div>
	)
}
