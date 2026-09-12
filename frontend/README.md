# Festival Cinema - Frontend (Sezione 10)

Single Page Application React che consuma le API REST esposte dal backend
Spring Boot sotto `/api/**` (vedi `FestivalApiController`, `FilmApiController`,
`RegistaApiController`, `SalaApiController`, `ProiezioneApiController`,
`RecensioneApiController`, `AuthApiController`).

L'autenticazione non e' basata su token: la SPA riusa il login a
form/sessione gia' esposto dal sito Thymeleaf (`POST /login`, `POST /logout`),
abilitato in CORS con credenziali per l'origin `http://localhost:5173` (vedi
`CorsConfig` e `SecurityConfig` nel progetto backend).

## Avvio

Richiede il backend gia' avviato su `http://localhost:8080` (con PostgreSQL
configurato, vedi `application.properties` del progetto principale).

```bash
npm install
npm run dev
```

L'app e' raggiungibile su `http://localhost:5173`.

## Struttura

- `src/api.js` — helper `fetch` verso il backend (base URL, gestione errori,
  sessione via cookie).
- `src/context/AuthContext.jsx` — stato di autenticazione condiviso (chi e'
  loggato, se e' ADMIN), popolato da `GET /api/me`.
- `src/pages/` — una pagina per ciascun caso d'uso di navigazione: elenco e
  dettaglio festival, ricerca e dettaglio film (con recensioni), login,
  registrazione, pannello admin.
