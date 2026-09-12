import { createContext, useCallback, useContext, useEffect, useState } from 'react'
import { api, login as apiLogin, logout as apiLogout } from '../api.js'

const AuthContext = createContext(null)

// Stato di autenticazione condiviso da tutta la SPA, popolato da GET /api/me
// (vedi AuthApiController). Non esiste un token: la sessione e' un cookie
// gestito dal backend, quindi qui teniamo solo la "proiezione" di chi e'
// l'utente corrente cosi' come la vede il server in questo momento.
export function AuthProvider({ children }) {
	const [utente, setUtente] = useState(null)
	const [caricamento, setCaricamento] = useState(true)

	const ricarica = useCallback(async () => {
		try {
			const stato = await api.get('/api/me')
			setUtente(stato.autenticato ? stato : null)
		} catch {
			// Backend irraggiungibile o sessione scaduta: si tratta l'utente come
			// anonimo invece di lasciare una promise rifiutata non gestita.
			setUtente(null)
		}
	}, [])

	useEffect(() => {
		ricarica().finally(() => setCaricamento(false))
	}, [ricarica])

	async function login(username, password) {
		await apiLogin(username, password)
		await ricarica()
	}

	async function logout() {
		await apiLogout()
		setUtente(null)
	}

	return (
		<AuthContext.Provider value={{ utente, caricamento, login, logout }}>
			{children}
		</AuthContext.Provider>
	)
}

export function useAuth() {
	return useContext(AuthContext)
}
