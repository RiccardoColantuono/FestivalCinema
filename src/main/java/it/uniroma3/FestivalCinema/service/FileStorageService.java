package it.uniroma3.FestivalCinema.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

// Upload di locandine/immagini: i file vengono salvati su disco (non nel database,
// per non appesantire le query JPA) sotto app.upload.dir, con un nome generato per
// evitare collisioni. Il nome restituito e' quello persistito sull'entita'
// (Film.locandina, Festival.immagineCopertina) e usato per ricostruire l'URL
// pubblico /uploads/<nome> (vedi WebConfig, che espone la cartella come risorsa
// statica sia al sito Thymeleaf sia alla SPA React).
@Service
public class FileStorageService {

	private static final long DIMENSIONE_MASSIMA_BYTE = 5L * 1024 * 1024;

	private final Path directory;

	public FileStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) {
		this.directory = Path.of(uploadDir).toAbsolutePath().normalize();
		try {
			Files.createDirectories(this.directory);
		} catch (IOException e) {
			throw new IllegalStateException("Impossibile creare la cartella di upload: " + this.directory, e);
		}
	}

	public String salva(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			return null;
		}
		if (file.getSize() > DIMENSIONE_MASSIMA_BYTE) {
			throw new IllegalArgumentException("Il file supera la dimensione massima consentita (5MB)");
		}
		String contentType = file.getContentType();
		if (contentType == null || !contentType.startsWith("image/")) {
			throw new IllegalArgumentException("Il file caricato deve essere un'immagine");
		}

		String estensione = "";
		String nomeOriginale = file.getOriginalFilename();
		if (nomeOriginale != null && nomeOriginale.contains(".")) {
			estensione = nomeOriginale.substring(nomeOriginale.lastIndexOf('.'));
		}
		String nomeFile = UUID.randomUUID() + estensione;

		try (InputStream input = file.getInputStream()) {
			Files.copy(input, this.directory.resolve(nomeFile), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new IllegalStateException("Impossibile salvare il file caricato", e);
		}
		return nomeFile;
	}
}
