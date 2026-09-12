import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'
import { Errore } from '../components/Stato.jsx'

export default function LoginPage() {
	const { login } = useAuth()
	const navigate = useNavigate()
	const [username, setUsername] = useState('')
	const [password, setPassword] = useState('')
	const [errore, setErrore] = useState(null)

	async function handleSubmit(e) {
		e.preventDefault()
		setErrore(null)
		try {
			await login(username, password)
			navigate('/')
		} catch {
			setErrore(new Error('Accesso non riuscito: controlla username e password'))
		}
	}

	return (
		<div className="container">
			<h2>👋 Accedi</h2>
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
				<button className="btn" type="submit">Accedi</button>
			</form>
		</div>
	)
}
