package it.uniroma3.FestivalCinema.exception;

public class UsernameGiaRegistratoException extends RuntimeException {

	public UsernameGiaRegistratoException(String username) {
		super("Lo username '" + username + "' e' gia' in uso");
	}
}
