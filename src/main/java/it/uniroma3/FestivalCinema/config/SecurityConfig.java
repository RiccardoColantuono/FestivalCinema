package it.uniroma3.FestivalCinema.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final DataSource dataSource;

	public SecurityConfig(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// Autenticazione basata sulla tabella utente esistente (Sezione 5): niente
	// tabelle di Spring Security aggiuntive, si riusano username/password/ruolo.
	@Bean
	public UserDetailsService userDetailsService() {
		JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);

		manager.setUsersByUsernameQuery(
			"select username, password, true as enabled from utente where username = ?"
		);

		manager.setAuthoritiesByUsernameQuery(
			"select username, ruolo as authority from utente where username = ?"
		);

		return manager;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.cors(Customizer.withDefaults())
			// La protezione CSRF di default si basa su un token legato alla sessione
			// HTTP, pensato per form Thymeleaf same-origin (che lo ricevono gia'
			// automaticamente via thymeleaf-extras-springsecurity6). Il frontend
			// React (Sezione 10) e' pero' su un'altra origin e non avrebbe modo di
			// leggere quel token: per le sole rotte che consuma (l'API e il login/
			// logout a sessione condivisi con il sito Thymeleaf) il rischio residuo
			// e' comunque mitigato dall'allowlist rigida di CorsConfig, che impedisce
			// a un'origin diversa da localhost:5173 di completare queste richieste.
			.csrf(csrf -> csrf.ignoringRequestMatchers("/api/**", "/login", "/logout"))
			// Sezione 5: funzionalita' pubbliche libere, recensioni protette,
			// area /admin/** riservata al ruolo ADMIN.
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(HttpMethod.GET,
					"/", "/login", "/register", "/error",
					"/festival", "/festival/**",
					"/film", "/film/**",
					"/api/**",
					"/css/**", "/js/**", "/images/**"
				).permitAll()
				.requestMatchers(HttpMethod.POST, "/register").permitAll()
				.requestMatchers("/recensioni/**").authenticated()
				.requestMatchers(HttpMethod.POST, "/api/film/*/recensioni").authenticated()
				.requestMatchers(HttpMethod.PUT, "/api/recensioni/**").authenticated()
				.requestMatchers(HttpMethod.DELETE, "/api/recensioni/**").authenticated()
				.requestMatchers("/admin/**").hasAnyAuthority("ADMIN")
				.requestMatchers(HttpMethod.POST, "/api/festival/**", "/api/film/**", "/api/registi/**",
					"/api/sale/**", "/api/proiezioni/**").hasAnyAuthority("ADMIN")
				.requestMatchers(HttpMethod.PUT, "/api/festival/**", "/api/proiezioni/**")
					.hasAnyAuthority("ADMIN")
				.requestMatchers(HttpMethod.DELETE, "/api/proiezioni/**", "/api/festival/**")
					.hasAnyAuthority("ADMIN")
				.anyRequest().authenticated()
			)
			.formLogin(form -> form
				.loginPage("/login")
				.defaultSuccessUrl("/", true)
				.permitAll()
			)
			.logout(logout -> logout
				.logoutUrl("/logout")
				.logoutSuccessUrl("/")
				.permitAll()
			);

		return http.build();
	}
}
