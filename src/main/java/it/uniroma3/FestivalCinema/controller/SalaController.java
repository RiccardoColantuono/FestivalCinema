package it.uniroma3.FestivalCinema.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.FestivalCinema.model.Sala;
import it.uniroma3.FestivalCinema.service.SalaService;
import jakarta.validation.Valid;

// Gestione sale (Sezione 4.3): come Regista, nessuna pagina pubblica dedicata,
// gestione interamente sotto /admin (accesso riservato ad ADMIN).
@Controller
public class SalaController {

	private final SalaService salaService;

	public SalaController(SalaService salaService) {
		this.salaService = salaService;
	}

	@GetMapping("/admin/sale")
	public String elenco(Model model) {
		model.addAttribute("sale", this.salaService.findAll());
		return "admin/sala/list";
	}

	@GetMapping("/admin/sale/nuova")
	public String formNuova(Model model) {
		model.addAttribute("sala", new Sala());
		return "admin/sala/form";
	}

	@PostMapping("/admin/sale")
	public String crea(@Valid @ModelAttribute("sala") Sala sala, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "admin/sala/form";
		}
		this.salaService.salva(sala);
		return "redirect:/admin/sale";
	}

	@GetMapping("/admin/sale/{id}/modifica")
	public String formModifica(@PathVariable Long id, Model model) {
		Optional<Sala> salaOptional = this.salaService.findById(id);
		if (salaOptional.isEmpty()) {
			return "redirect:/admin/sale";
		}
		model.addAttribute("sala", salaOptional.get());
		return "admin/sala/form";
	}

	@PostMapping("/admin/sale/{id}")
	public String aggiorna(@PathVariable Long id, @Valid @ModelAttribute("sala") Sala salaForm,
	                        BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			salaForm.setId(id);
			return "admin/sala/form";
		}
		this.salaService.aggiorna(id, salaForm);
		return "redirect:/admin/sale";
	}

	@PostMapping("/admin/sale/{id}/elimina")
	public String elimina(@PathVariable Long id) {
		this.salaService.elimina(id);
		return "redirect:/admin/sale";
	}
}
