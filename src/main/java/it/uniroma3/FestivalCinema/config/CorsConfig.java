package it.uniroma3.FestivalCinema.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/*
 * Permette al frontend React (Vite, http://localhost:5173) di chiamare gli
 * endpoint REST sotto /api/** esposti da questo backend (http://localhost:8080).
 * L'autenticazione della SPA riusa il login a form/sessione gia' esposto per il
 * sito Thymeleaf (POST /login, POST /logout): anche questi due path devono
 * quindi essere raggiungibili in CORS con credenziali, non solo /api/**.
 */
@Configuration
public class CorsConfig {

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("http://localhost:5173"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/api/**", configuration);
		source.registerCorsConfiguration("/login", configuration);
		source.registerCorsConfiguration("/logout", configuration);
		return source;
	}
}
