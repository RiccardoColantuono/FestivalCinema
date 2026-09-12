-- Dati di esempio, caricati automaticamente da Hibernate dopo la creazione dello
-- schema (spring.jpa.hibernate.ddl-auto=create). Nessun utente e' precaricato qui
-- di proposito: registrati dalla pagina /register, poi (se ti serve un account
-- ADMIN) esegui manualmente:
--   UPDATE utente SET ruolo = 'ADMIN' WHERE username = 'iltuoUsername';

INSERT INTO regista (nome, cognome, data_nascita, nazionalita) VALUES
('Hayao', 'Miyazaki', '1941-01-05', 'Giapponese'),
('Christopher', 'Nolan', '1970-07-30', 'Britannica'),
('Sofia', 'Coppola', '1971-05-14', 'Statunitense'),
('Bong', 'Joon-ho', '1969-09-14', 'Sudcoreana');

INSERT INTO sala (nome, indirizzo, capienza) VALUES
('Sala Grande', 'Via Roma 1, Roma', 300),
('Sala Rossa', 'Via Roma 1, Roma', 120),
('Arena Estiva', 'Piazza del Cinema 5, Roma', 500);

INSERT INTO film (titolo, anno, durata, genere, paese_produzione, regista_id) VALUES
('La citta'' incantata', 2001, 125, 'Animazione', 'Giappone',
	(SELECT id FROM regista WHERE cognome = 'Miyazaki')),
('Inception', 2010, 148, 'Fantascienza', 'USA',
	(SELECT id FROM regista WHERE cognome = 'Nolan')),
('Oppenheimer', 2023, 180, 'Biografico', 'USA',
	(SELECT id FROM regista WHERE cognome = 'Nolan')),
('Lost in Translation', 2003, 102, 'Drammatico', 'USA',
	(SELECT id FROM regista WHERE cognome = 'Coppola')),
('Parasite', 2019, 132, 'Thriller', 'Corea del Sud',
	(SELECT id FROM regista WHERE cognome = 'Joon-ho'));

INSERT INTO festival (nome, anno, citta, data_inizio, data_fine, descrizione) VALUES
('Festival del Cinema di Roma', 2026, 'Roma', '2026-10-14', '2026-10-24',
	'Rassegna internazionale di cinema d''autore'),
('Notti Fantascienza Fest', 2026, 'Torino', '2026-11-05', '2026-11-09',
	'Festival dedicato alla fantascienza e all''animazione');

INSERT INTO festival_film (festival_id, film_id)
SELECT f.id, m.id FROM festival f, film m
WHERE f.nome = 'Festival del Cinema di Roma'
AND m.titolo IN ('Oppenheimer', 'Lost in Translation', 'Parasite');

INSERT INTO festival_film (festival_id, film_id)
SELECT f.id, m.id FROM festival f, film m
WHERE f.nome = 'Notti Fantascienza Fest'
AND m.titolo IN ('La citta'' incantata', 'Inception');

INSERT INTO proiezione (data, ora, stato, festival_id, film_id, sala_id)
SELECT '2026-10-15', '20:30:00', 'SCHEDULED', f.id, m.id, s.id
FROM festival f, film m, sala s
WHERE f.nome = 'Festival del Cinema di Roma' AND m.titolo = 'Oppenheimer' AND s.nome = 'Sala Grande';

INSERT INTO proiezione (data, ora, stato, festival_id, film_id, sala_id)
SELECT '2026-10-16', '18:00:00', 'SCHEDULED', f.id, m.id, s.id
FROM festival f, film m, sala s
WHERE f.nome = 'Festival del Cinema di Roma' AND m.titolo = 'Parasite' AND s.nome = 'Sala Rossa';

INSERT INTO proiezione (data, ora, stato, festival_id, film_id, sala_id)
SELECT '2026-11-06', '21:00:00', 'SCHEDULED', f.id, m.id, s.id
FROM festival f, film m, sala s
WHERE f.nome = 'Notti Fantascienza Fest' AND m.titolo = 'La citta'' incantata' AND s.nome = 'Arena Estiva';
