import { Navigate, Route, Routes } from 'react-router-dom'
import Navbar from './components/Navbar.jsx'
import { useAuth } from './context/AuthContext.jsx'
import FestivalListPage from './pages/FestivalListPage.jsx'
import FestivalDetailPage from './pages/FestivalDetailPage.jsx'
import FilmListPage from './pages/FilmListPage.jsx'
import FilmDetailPage from './pages/FilmDetailPage.jsx'
import LoginPage from './pages/LoginPage.jsx'
import RegisterPage from './pages/RegisterPage.jsx'
import AdminPage from './pages/AdminPage.jsx'

export default function App() {
	const { utente, caricamento } = useAuth()

	return (
		<>
			<Navbar />
			<Routes>
				<Route path="/" element={<FestivalListPage />} />
				<Route path="/festival/:id" element={<FestivalDetailPage />} />
				<Route path="/film" element={<FilmListPage />} />
				<Route path="/film/:id" element={<FilmDetailPage />} />
				<Route path="/login" element={<LoginPage />} />
				<Route path="/registrati" element={<RegisterPage />} />
				<Route
					path="/admin"
					element={
						caricamento ? null : utente?.admin ? <AdminPage /> : <Navigate to="/" replace />
					}
				/>
			</Routes>
		</>
	)
}
