import { useEffect, useState } from 'react'
import { api, ApiError } from '../api.js'
import { Errore } from '../components/Stato.jsx'

// Pannello admin della SPA (Sezione 10): un fascio di piccoli form, uno per
// ciascuna operazione di scrittura gia' esposta dall'API REST. La gestione
// "ricca" (elenchi, modifica, eliminazione) resta nel sito Thymeleaf sotto
// /admin/**: qui si dimostra la stessa capacita' di scrittura consumando
// esclusivamente /api/**, come richiesto per il frontend disaccoppiato.
export default function AdminPage() {
	const [registi, setRegisti] = useState([])
	const [sale, setSale] = useState([])
	const [festival, setFestival] = useState([])
	const [film, setFilm] = useState([])

	function ricaricaTutto() {
		api.get('/api/registi').then(setRegisti).catch(() => {})
		api.get('/api/sale').then(setSale).catch(() => {})
		api.get('/api/festival').then(setFestival).catch(() => {})
		api.get('/api/movies').then(setFilm).catch(() => {})
	}

	useEffect(ricaricaTutto, [])

	return (
		<div className="container">
			<h2>Pannello admin</h2>
			<p className="messaggio">
				Ogni form qui sotto chiama direttamente un endpoint di <code>/api/**</code>.
			</p>

			<div className="admin-section">
				<NuovoRegistaForm onCreato={ricaricaTutto} />
			</div>
			<div className="admin-section">
				<NuovaSalaForm onCreato={ricaricaTutto} />
			</div>
			<div className="admin-section">
				<NuovoFestivalForm onCreato={ricaricaTutto} />
			</div>
			<div className="admin-section">
				<NuovoFilmForm registi={registi} onCreato={ricaricaTutto} />
			</div>
			<div className="admin-section">
				<AssociaFilmFestivalForm festival={festival} film={film} />
			</div>
			<div className="admin-section">
				<ProgrammaProiezioneForm festival={festival} film={film} sale={sale} />
			</div>
		</div>
	)
}

function useFormFeedback() {
	const [errore, setErrore] = useState(null)
	const [messaggio, setMessaggio] = useState(null)

	async function esegui(azione) {
		setErrore(null)
		setMessaggio(null)
		try {
			await azione()
			setMessaggio('Fatto.')
		} catch (err) {
			setErrore(err instanceof ApiError ? err : new Error('Operazione non riuscita'))
		}
	}

	return { errore, messaggio, esegui }
}

function NuovoRegistaForm({ onCreato }) {
	const [dati, setDati] = useState({ nome: '', cognome: '', dataNascita: '', nazionalita: '' })
	const { errore, messaggio, esegui } = useFormFeedback()

	function campo(nome) {
		return { value: dati[nome], onChange: (e) => setDati({ ...dati, [nome]: e.target.value }) }
	}

	async function handleSubmit(e) {
		e.preventDefault()
		await esegui(async () => {
			await api.post('/api/registi', { ...dati, dataNascita: dati.dataNascita || null })
			setDati({ nome: '', cognome: '', dataNascita: '', nazionalita: '' })
			onCreato()
		})
	}

	return (
		<form className="form-box" onSubmit={handleSubmit}>
			<h3 style={{ margin: 0 }}>Nuovo regista</h3>
			<label>Nome<input required {...campo('nome')} /></label>
			<label>Cognome<input required {...campo('cognome')} /></label>
			<label>Data di nascita<input type="date" {...campo('dataNascita')} /></label>
			<label>Nazionalita'<input {...campo('nazionalita')} /></label>
			<Errore errore={errore} />
			{messaggio && <p className="messaggio">{messaggio}</p>}
			<button className="btn" type="submit">Crea regista</button>
		</form>
	)
}

function NuovaSalaForm({ onCreato }) {
	const [dati, setDati] = useState({ nome: '', indirizzo: '', capienza: 100 })
	const { errore, messaggio, esegui } = useFormFeedback()

	function campo(nome) {
		return { value: dati[nome], onChange: (e) => setDati({ ...dati, [nome]: e.target.value }) }
	}

	async function handleSubmit(e) {
		e.preventDefault()
		await esegui(async () => {
			await api.post('/api/sale', { ...dati, capienza: Number(dati.capienza) })
			setDati({ nome: '', indirizzo: '', capienza: 100 })
			onCreato()
		})
	}

	return (
		<form className="form-box" onSubmit={handleSubmit}>
			<h3 style={{ margin: 0 }}>Nuova sala</h3>
			<label>Nome<input required {...campo('nome')} /></label>
			<label>Indirizzo<input {...campo('indirizzo')} /></label>
			<label>Capienza<input type="number" min="1" required {...campo('capienza')} /></label>
			<Errore errore={errore} />
			{messaggio && <p className="messaggio">{messaggio}</p>}
			<button className="btn" type="submit">Crea sala</button>
		</form>
	)
}

function NuovoFestivalForm({ onCreato }) {
	const [dati, setDati] = useState({ nome: '', anno: '', citta: '', dataInizio: '', dataFine: '', descrizione: '' })
	const { errore, messaggio, esegui } = useFormFeedback()

	function campo(nome) {
		return { value: dati[nome], onChange: (e) => setDati({ ...dati, [nome]: e.target.value }) }
	}

	async function handleSubmit(e) {
		e.preventDefault()
		await esegui(async () => {
			await api.post('/api/festival', { ...dati, anno: Number(dati.anno) })
			setDati({ nome: '', anno: '', citta: '', dataInizio: '', dataFine: '', descrizione: '' })
			onCreato()
		})
	}

	return (
		<form className="form-box" onSubmit={handleSubmit}>
			<h3 style={{ margin: 0 }}>Nuovo festival</h3>
			<label>Nome<input required {...campo('nome')} /></label>
			<label>Anno<input type="number" required {...campo('anno')} /></label>
			<label>Citta'<input {...campo('citta')} /></label>
			<label>Data inizio<input type="date" required {...campo('dataInizio')} /></label>
			<label>Data fine<input type="date" required {...campo('dataFine')} /></label>
			<label>Descrizione<textarea rows="3" {...campo('descrizione')} /></label>
			<Errore errore={errore} />
			{messaggio && <p className="messaggio">{messaggio}</p>}
			<button className="btn" type="submit">Crea festival</button>
		</form>
	)
}

function NuovoFilmForm({ registi, onCreato }) {
	const [dati, setDati] = useState({
		titolo: '', anno: '', durata: '', genere: '', paeseProduzione: '', registaId: '',
	})
	const { errore, messaggio, esegui } = useFormFeedback()

	function campo(nome) {
		return { value: dati[nome], onChange: (e) => setDati({ ...dati, [nome]: e.target.value }) }
	}

	async function handleSubmit(e) {
		e.preventDefault()
		await esegui(async () => {
			const corpo = {
				titolo: dati.titolo,
				anno: Number(dati.anno),
				durata: Number(dati.durata),
				genere: dati.genere,
				paeseProduzione: dati.paeseProduzione,
				// Il regista e' obbligatorio sull'entita' Film: lo si include anche nel
				// corpo (oltre che come query param, richiesto da FilmApiController)
				// cosi' la validazione @NotNull lo trova gia' risolto.
				regista: { id: Number(dati.registaId) },
			}
			await api.post(`/api/movies?registaId=${dati.registaId}`, corpo)
			setDati({ titolo: '', anno: '', durata: '', genere: '', paeseProduzione: '', registaId: '' })
			onCreato()
		})
	}

	return (
		<form className="form-box" onSubmit={handleSubmit}>
			<h3 style={{ margin: 0 }}>Nuovo film</h3>
			<label>Titolo<input required {...campo('titolo')} /></label>
			<label>Anno<input type="number" required {...campo('anno')} /></label>
			<label>Durata (minuti)<input type="number" required {...campo('durata')} /></label>
			<label>Genere<input {...campo('genere')} /></label>
			<label>Paese di produzione<input {...campo('paeseProduzione')} /></label>
			<label>
				Regista
				<select required value={dati.registaId} onChange={(e) => setDati({ ...dati, registaId: e.target.value })}>
					<option value="">-- seleziona --</option>
					{registi.map((r) => (
						<option key={r.id} value={r.id}>{r.nome} {r.cognome}</option>
					))}
				</select>
			</label>
			<Errore errore={errore} />
			{messaggio && <p className="messaggio">{messaggio}</p>}
			<button className="btn" type="submit">Crea film</button>
		</form>
	)
}

function AssociaFilmFestivalForm({ festival, film }) {
	const [festivalId, setFestivalId] = useState('')
	const [filmId, setFilmId] = useState('')
	const { errore, messaggio, esegui } = useFormFeedback()

	async function handleSubmit(e) {
		e.preventDefault()
		await esegui(() => api.post(`/api/festival/${festivalId}/movies/${filmId}`))
	}

	return (
		<form className="form-box" onSubmit={handleSubmit}>
			<h3 style={{ margin: 0 }}>Associa un film a un festival</h3>
			<label>
				Festival
				<select required value={festivalId} onChange={(e) => setFestivalId(e.target.value)}>
					<option value="">-- seleziona --</option>
					{festival.map((f) => <option key={f.id} value={f.id}>{f.nome} ({f.anno})</option>)}
				</select>
			</label>
			<label>
				Film
				<select required value={filmId} onChange={(e) => setFilmId(e.target.value)}>
					<option value="">-- seleziona --</option>
					{film.map((f) => <option key={f.id} value={f.id}>{f.titolo}</option>)}
				</select>
			</label>
			<Errore errore={errore} />
			{messaggio && <p className="messaggio">{messaggio}</p>}
			<button className="btn" type="submit">Associa</button>
		</form>
	)
}

function ProgrammaProiezioneForm({ festival, film, sale }) {
	const [dati, setDati] = useState({ festivalId: '', filmId: '', salaId: '', data: '', ora: '' })
	const { errore, messaggio, esegui } = useFormFeedback()

	function campo(nome) {
		return { value: dati[nome], onChange: (e) => setDati({ ...dati, [nome]: e.target.value }) }
	}

	async function handleSubmit(e) {
		e.preventDefault()
		await esegui(() => api.post('/api/proiezioni', {
			festivalId: Number(dati.festivalId),
			filmId: Number(dati.filmId),
			salaId: Number(dati.salaId),
			data: dati.data,
			ora: dati.ora,
		}))
	}

	return (
		<form className="form-box" onSubmit={handleSubmit}>
			<h3 style={{ margin: 0 }}>Programma una proiezione</h3>
			<label>
				Festival
				<select required value={dati.festivalId} onChange={(e) => setDati({ ...dati, festivalId: e.target.value })}>
					<option value="">-- seleziona --</option>
					{festival.map((f) => <option key={f.id} value={f.id}>{f.nome} ({f.anno})</option>)}
				</select>
			</label>
			<label>
				Film
				<select required value={dati.filmId} onChange={(e) => setDati({ ...dati, filmId: e.target.value })}>
					<option value="">-- seleziona --</option>
					{film.map((f) => <option key={f.id} value={f.id}>{f.titolo}</option>)}
				</select>
			</label>
			<label>
				Sala
				<select required value={dati.salaId} onChange={(e) => setDati({ ...dati, salaId: e.target.value })}>
					<option value="">-- seleziona --</option>
					{sale.map((s) => <option key={s.id} value={s.id}>{s.nome} ({s.capienza} posti)</option>)}
				</select>
			</label>
			<label>Data<input type="date" required {...campo('data')} /></label>
			<label>Ora<input type="time" required {...campo('ora')} /></label>
			<Errore errore={errore} />
			{messaggio && <p className="messaggio">{messaggio}</p>}
			<button className="btn" type="submit">Programma</button>
		</form>
	)
}
