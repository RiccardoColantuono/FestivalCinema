package it.uniroma3.FestivalCinema.config;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Espone la cartella di upload (locandine/immagini) come risorse statiche
// raggiungibili da /uploads/**, sia dal sito Thymeleaf sia dalla SPA React
// (vedi FileStorageService, e SecurityConfig per la regola di accesso pubblico).
@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final String uploadDir;

	public WebConfig(@Value("${app.upload.dir:uploads}") String uploadDir) {
		this.uploadDir = uploadDir;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String location = "file:" + Path.of(this.uploadDir).toAbsolutePath().normalize() + "/";
		registry.addResourceHandler("/uploads/**").addResourceLocations(location);
	}
}
