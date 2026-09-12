import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api.js'
import { Caricamento, Errore } from '../components/Stato.jsx'

export default function FestivalListPage() {
	const [festival, setFestival] = useState(null)
	const [errore, setErrore] = useState(null)

	useEffect(() => {
		api.get('/api/festival').then(setFestival).catch(setErrore)
	}, [])

	return (
		<div className="container">
			<div className="page-header">
				<h2>Festival cinematografici</h2>
			</div>
			<Errore errore={errore} />
			{!festival && !errore && <Caricamento />}
			{festival && festival.length === 0 && <p className="messaggio">Nessun festival disponibile.</p>}
			<div className="grid">
				{festival?.map((f) => (
					<div className="card" key={f.id}>
						<h3>{f.nome}</h3>
						<p className="meta">{f.citta} - {f.anno}</p>
						<p className="meta">{f.dataInizio} &rarr; {f.dataFine}</p>
						<Link className="btn" to={`/festival/${f.id}`}>Dettagli</Link>
					</div>
				))}
			</div>
		</div>
	)
}
