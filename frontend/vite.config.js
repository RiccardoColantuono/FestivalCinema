import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Sezione 10: la SPA gira su http://localhost:5173 e chiama il backend Spring
// Boot su http://localhost:8080/api/**, gia' abilitato in CORS (vedi CorsConfig
// nel progetto backend).
export default defineConfig({
	plugins: [react()],
	server: {
		port: 5173,
	},
})
