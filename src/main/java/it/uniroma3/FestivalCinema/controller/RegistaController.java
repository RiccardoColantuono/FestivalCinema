package it.uniroma3.FestivalCinema.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.FestivalCinema.model.Regista;
import it.uniroma3.FestivalCinema.service.RegistaService;
import jakarta.validation.Valid;

// Gestione registi (Sezione 4.3): a differenza di Festival e Film, il regista
// non ha una pagina pubblica dedicata, quindi l'intera sezione vive sotto /admin
// (accesso riservato ad ADMIN, vedi SecurityConfig) ed e' raggiungibile dal menu.
@Controller
public class RegistaController {

	private final RegistaService registaService;

	public RegistaController(RegistaService registaService) {
		this.registaService = registaService;
	}

	@GetMapping("/admin/registi")
	public String elenco(Model model) {
		model.addAttribute("registi", this.registaService.findAll());
		return "admin/regista/list";
	}

	@GetMapping("/admin/registi/nuovo")
	public String formNuovo(Model model) {
		model.addAttribute("regista", new Regista());
		return "admin/regista/form";
	}

	@PostMapping("/admin/registi")
	public String crea(@Valid @ModelAttribute("regista") Regista regista, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "admin/regista/form";
		}
		this.registaService.salva(regista);
		return "redirect:/admin/registi";
	}

	@GetMapping("/admin/registi/{id}/modifica")
	public String formModifica(@PathVariable Long id, Model model) {
		Optional<Regista> registaOptional = this.registaService.findById(id);
		if (registaOptional.isEmpty()) {
			return "redirect:/admin/registi";
		}
		model.addAttribute("regista", registaOptional.get());
		return "admin/regista/form";
	}

	@PostMapping("/admin/registi/{id}")
	public String aggiorna(@PathVariable Long id, @Valid @ModelAttribute("regista") Regista registaForm,
	                        BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			registaForm.setId(id);
			return "admin/regista/form";
		}
		this.registaService.aggiorna(id, registaForm);
		return "redirect:/admin/registi";
	}

	@PostMapping("/admin/registi/{id}/elimina")
	public String elimina(@PathVariable Long id) {
		this.registaService.elimina(id);
		return "redirect:/admin/registi";
	}
}
