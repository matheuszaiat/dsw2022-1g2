package br.unirio.dsw.compartilhador.api.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe responsável pela validação de diversos tipos de campos
 * 
 * @author marciobarros
 */
public class ValidationUtils
{
	/**
	 * Valida um campo do tipo e-mail
	 */
	public static boolean validEmail(String email)
	{
		if (email == null)
			return false;
		
		String EMAIL_PATTERN = "^[A-Z0-9._%-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$";
		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(email.toUpperCase());
		return matcher.matches();
	}

	/**
	 * Verifica se uma senha é aceitável, checando se ela tem pelo menos 8 caracteres, uma letra e um número
	 */
	public static boolean validPassword(String password)
	{
		if (password == null)
			return false;
		
		return (password.length() >= 8) && password.matches(".*[a-zA-Z].*") && password.matches(".*[0-9].*");
	}
	

	/**
	 * Valida uma data de acordo com o formato "aaaa-mm-dd"
	 */
	public static boolean validDate(String date) {
		
		if (date == null)
			return false;
		
		return (date.matches("^((19|2[0-9])[0-9]{2})-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$"));
	}
	
}