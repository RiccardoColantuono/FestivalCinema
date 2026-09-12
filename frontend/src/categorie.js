// Colori ed emoji per categoria (nav) e per genere/stato, condivisi da tutte le
// pagine. Stesso linguaggio visivo usato nel sito Thymeleaf (vedi
// fragments/common.html lato backend), per coerenza tra le due interfacce.

export const EMOJI_GENERE = {
	Animazione: '🎨',
	Fantascienza: '🚀',
	Drammatico: '🎭',
	Thriller: '🔪',
	Commedia: '😂',
	Horror: '👻',
	Documentario: '🎥',
	Azione: '💥',
	Romantico: '❤️',
	Biografico: '📖',
	Musical: '🎵',
	Fantasy: '🧙',
	Avventura: '🗺️',
}

export function emojiGenere(genere) {
	return EMOJI_GENERE[genere] || '🎞️'
}

export const STATO_PROIEZIONE = {
	SCHEDULED: { emoji: '🟢', etichetta: 'Programmata', classe: 'stato-scheduled' },
	COMPLETED: { emoji: '✅', etichetta: 'Conclusa', classe: 'stato-completed' },
	CANCELLED: { emoji: '❌', etichetta: 'Annullata', classe: 'stato-cancelled' },
}

export function statoProiezione(stato) {
	return STATO_PROIEZIONE[stato] || { emoji: '', etichetta: stato, classe: '' }
}

export const MEDAGLIE = ['🥇', '🥈', '🥉']
