import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { registrati } from '../api.js'
import { Errore } from '../components/Stato.jsx'

export default function RegisterPage() {
	const navigate = useNavigate()
	const [username, setUsername] = useState('')
	const [password, setPassword] = useState('')
	const [errore, setErrore] = useState(null)

	async function handleSubmit(e) {
		e.preventDefault()
		setErrore(null)
		try {
			await registrati(username, password)
			navigate('/login')
		} catch {
			setErrore(new Error('Registrazione non riuscita: username gia\' in uso?'))
		}
	}

	return (
		<div className="container">
			<h2>Registrati</h2>
			<form className="form-box" onSubmit={handleSubmit}>
				<label>
					Username
					<input value={username} onChange={(e) => setUsername(e.target.value)} required />
				</label>
				<label>
					Password
					<input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
				</label>
				<Errore errore={errore} />
				<button className="btn" type="submit">Registrati</button>
			</form>
		</div>
	)
}
