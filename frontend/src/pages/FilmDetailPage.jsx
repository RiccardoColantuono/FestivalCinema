import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { api, ApiError } from '../api.js'
import { Caricamento, Errore } from '../components/Stato.jsx'
import { useAuth } from '../context/AuthContext.jsx'
import { emojiGenere } from '../categorie.js'

export default function FilmDetailPage() {
	const { id } = useParams()
	const { utente } = useAuth()
	const [film, setFilm] = useState(null)
	const [recensioni, setRecensioni] = useState(null)
	const [statistiche, setStatistiche] = useState(null)
	const [errore, setErrore] = useState(null)
	const [nuovaRecensione, setNuovaRecensione] = useState({ voto: 5, testo: '' })
	const [erroreInvio, setErroreInvio] = useState(null)

	function ricaricaRecensioni() {
		api.get(`/api/movies/${id}/reviews`).then(setRecensioni).catch(setErrore)
		api.get(`/api/movies/${id}/statistiche`).then(setStatistiche).catch(() => {})
	}

	useEffect(() => {
		setFilm(null)
		setRecensioni(null)
		setStatistiche(null)
		Promise.all([
			api.get(`/api/movies/${id}`),
			api.get(`/api/movies/${id}/reviews`),
			api.get(`/api/movies/${id}/statistiche`),
		])
			.then(([f, r, s]) => {
				setFilm(f)
				setRecensioni(r)
				setStatistiche(s)
			})
			.catch(setErrore)
	}, [id])

	async function handleSubmit(e) {
		e.preventDefault()
		setErroreInvio(null)
		try {
			await api.post(`/api/movies/${id}/reviews`, nuovaRecensione)
			setNuovaRecensione({ voto: 5, testo: '' })
			ricaricaRecensioni()
		} catch (err) {
			setErroreInvio(err instanceof ApiError ? err : new Error('Invio non riuscito'))
		}
	}

	if (errore) return <div className="container"><Errore errore={errore} /></div>
	if (!film) return <div className="container"><Caricamento /></div>

	return (
		<div className="container">
			<div style={{ display: 'flex', gap: '1.5rem', alignItems: 'flex-start' }}>
				{film.locandina && (
					<img src={`http://localhost:8080/uploads/${film.locandina}`} alt=""
					     style={{ width: 160, height: 230, objectFit: 'cover', borderRadius: 8 }} />
				)}
				<div>
					<h1>{film.titolo}</h1>
					<p className="meta">{film.anno} &bull; {film.durata} min &bull; {film.paeseProduzione}</p>
					{film.genere && (
						<p><span className="badge-genere">{emojiGenere(film.genere)} {film.genere}</span></p>
					)}
					{film.regista && <p>🎬 Regia di <strong>{film.regista.nome} {film.regista.cognome}</strong></p>}
				</div>
			</div>

			{statistiche && statistiche.numeroRecensioni > 0 && (
				<p style={{ marginTop: '0.75rem' }}>
					<strong>{statistiche.votoMedio.toFixed(1)}/5</strong>
					<span className="meta"> ({statistiche.numeroRecensioni} recensioni)</span>
				</p>
			)}

			<h3 style={{ marginTop: '2rem' }}>💬 Recensioni</h3>
			{recensioni?.length === 0 && <p className="messaggio">Nessuna recensione ancora.</p>}
			{recensioni?.map((r) => (
				<div className="review" key={r.id}>
					<strong>{r.autoreUsername}</strong> - {r.voto}/5
					<p style={{ margin: '0.25rem 0 0' }}>{r.testo}</p>
				</div>
			))}

			{utente ? (
				<form className="form-box" style={{ marginTop: '1.5rem' }} onSubmit={handleSubmit}>
					<h3 style={{ margin: 0 }}>✍️ Lascia una recensione</h3>
					<label>
						Voto (1-5)
						<input type="number" min="1" max="5" required
						       value={nuovaRecensione.voto}
						       onChange={(e) => setNuovaRecensione({ ...nuovaRecensione, voto: Number(e.target.value) })} />
					</label>
					<label>
						Testo
						<textarea rows="3" required value={nuovaRecensione.testo}
						          onChange={(e) => setNuovaRecensione({ ...nuovaRecensione, testo: e.target.value })} />
					</label>
					<Errore errore={erroreInvio} />
					<button className="btn" type="submit">Invia recensione</button>
				</form>
			) : (
				<p className="messaggio" style={{ marginTop: '1rem' }}>Accedi per lasciare una recensione.</p>
			)}
		</div>
	)
}
