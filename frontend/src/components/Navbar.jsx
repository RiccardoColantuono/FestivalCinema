import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'

export default function Navbar() {
	const { utente, logout } = useAuth()
	const navigate = useNavigate()

	async function handleLogout() {
		await logout()
		navigate('/')
	}

	return (
		<nav className="navbar">
			<div className="navbar-inner">
				<Link className="brand" to="/">🎬 Festival Cinema</Link>
				<div className="nav-links">
					<Link className="cat-festival" to="/">🎪 Festival</Link>
					<Link className="cat-film" to="/film">🎬 Film</Link>
					<Link className="cat-proiezioni" to="/proiezioni">🕒 Proiezioni</Link>
					<Link className="cat-statistiche" to="/statistiche">📊 Statistiche</Link>
					{utente?.admin && <Link className="cat-admin" to="/admin">🛠️ Admin</Link>}
				</div>
				<div className="nav-auth">
					{utente ? (
						<>
							<span>Ciao, {utente.username}</span>
							<button onClick={handleLogout}>Esci</button>
						</>
					) : (
						<>
							<Link to="/login">Accedi</Link>
							<Link to="/registrati">Registrati</Link>
						</>
					)}
				</div>
			</div>
		</nav>
	)
}
