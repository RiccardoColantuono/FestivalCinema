const API_BASE = 'http://localhost:8080'

// Errore applicativo: incapsula lo status HTTP e il messaggio restituito dal
// backend (vedi ApiExceptionHandler: { status, errore, messaggio }).
export class ApiError extends Error {
	constructor(status, messaggio) {
		super(messaggio)
		this.status = status
	}
}

async function richiesta(path, options = {}) {
	const risposta = await fetch(`${API_BASE}${path}`, {
		credentials: 'include',
		headers: options.body ? { 'Content-Type': 'application/json' } : undefined,
		...options,
	})

	if (risposta.status === 204) {
		return null
	}

	const testo = await risposta.text()
	const corpo = testo ? JSON.parse(testo) : null

	if (!risposta.ok) {
		const messaggio = corpo?.messaggio || `Errore ${risposta.status}`
		throw new ApiError(risposta.status, messaggio)
	}

	return corpo
}

export const api = {
	get: (path) => richiesta(path),
	post: (path, body) => richiesta(path, { method: 'POST', body: body != null ? JSON.stringify(body) : undefined }),
	put: (path, body) => richiesta(path, { method: 'PUT', body: JSON.stringify(body) }),
	delete: (path) => richiesta(path, { method: 'DELETE' }),
}

// Login/logout/registrazione riusano gli endpoint a form/sessione del sito
// Thymeleaf (non sono sotto /api/**), percio' usano un content-type diverso.
export async function login(username, password) {
	await fetch(`${API_BASE}/login`, {
		method: 'POST',
		credentials: 'include',
		headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
		body: new URLSearchParams({ username, password }),
	})
}

export async function logout() {
	await fetch(`${API_BASE}/logout`, { method: 'POST', credentials: 'include' })
}

export async function registrati(username, password) {
	await fetch(`${API_BASE}/register`, {
		method: 'POST',
		credentials: 'include',
		headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
		body: new URLSearchParams({ username, password }),
	})
}
