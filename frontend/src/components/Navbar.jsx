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
				<Link className="brand" to="/">Festival Cinema</Link>
				<div className="nav-links">
					<Link to="/">Festival</Link>
					<Link to="/film">Film</Link>
					{utente?.admin && <Link to="/admin">Admin</Link>}
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
