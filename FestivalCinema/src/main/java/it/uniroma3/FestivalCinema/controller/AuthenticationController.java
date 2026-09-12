package it.uniroma3.FestivalCinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.FestivalCinema.service.UtenteService;

@Controller
public class AuthenticationController {

	private final UtenteService utenteService;

	public AuthenticationController(UtenteService utenteService) {
		this.utenteService = utenteService;
	}

	@PostMapping("/register")
	public String registra(@RequestParam String username, @RequestParam String password) {
		this.utenteService.registra(username, password);
		return "redirect:/login?registrato";
	}
}
