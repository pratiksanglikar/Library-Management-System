package edu.cmpe275.team13.controllers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.cmpe275.team13.beans.Librarian;
import edu.cmpe275.team13.beans.Patron;
import edu.cmpe275.team13.persistence.LibrarianDAOImpl;
import edu.cmpe275.team13.persistence.PatronDAOImpl;
import edu.cmpe275.util.Mailmail;

@Controller
public class PatronController {

	@Autowired
	private PatronDAOImpl patronDAO;

	@Autowired
	private LibrarianDAOImpl libDao;

	/*
	 * ClassPathXmlApplicationContext context = new
	 * ClassPathXmlApplicationContext("servlet-context.xml");
	 */
	// ClassPathXmlApplicationContext context = new
	// ClassPathXmlApplicationContext("beans.xml");
	// Mailmail mail = context.getBean(Mailmail.class);

	// @Autowired
	private Mailmail mail = new Mailmail();

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String getPatronLogin(Locale locale, Model model) {
		return "patronLogin";
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String patronLogin(@RequestParam("email") String email, @RequestParam("password") String password,
			Model model) throws NoSuchAlgorithmException {

		if (email.contains("@sjsu.edu")) {
			Librarian librarian = libDao.validateLibrarian(email, getSHADigest(password));
			if (librarian == null) {
				model.addAttribute("error", "Username and password do not match");
			} else {
				if (librarian.isLibrarian_verified()) {
					return "loginHome";
				} else {
					model.addAttribute("error", "Please verify your email.");
				}
			}
		} else {
			Patron patron = patronDAO.validatePatron(email, getSHADigest(password));
			if (patron == null) {
				model.addAttribute("error", "Username and password do not match");
			} else {
				if (patron.isPatron_verified()) {
					return "loginHome";
				} else {
					model.addAttribute("error", "Please verify your email.");
				}
			}
		}

		return "login";
	}

	@RequestMapping(value = "/librarianActivationlink", method = RequestMethod.GET)
	public String librarianUpdateActivationLink(@RequestParam("key") String key, Model model) {

		byte[] b = Base64.decodeBase64(key);
		// System.out.println(key);
		String decodedKey = new String(b);
		// System.out.println(decodedKey);
		// System.out.println("libbb");
		if (decodedKey.contains(":")) {
			// System.out.println("inside if");
			String[] s = decodedKey.split(":");
			int patron_id = Integer.parseInt(s[0]);
			String username = s[1];

			// System.out.println(username);
			Librarian librarian = libDao.getLibrarian(patron_id);
			librarian.setLibrarian_verified(true);
			libDao.updateLibrarian(librarian);
			// System.out.println("Librarian account verified");

		}
		return "home";
	}

	@RequestMapping(value = "/signUp", method = RequestMethod.GET)
	public String signUp(Locale locale, Model model) {
		return "home";
	}

	@RequestMapping(value = "/signUp", method = RequestMethod.POST)
	public String createPatron(@RequestParam("email") String patron_email,
			@RequestParam("password") String patron_password, @RequestParam("name") String patron_name,
			@RequestParam("studentid") int patron_id, Model model) throws NoSuchAlgorithmException {
		// System.out.println("sdsd");

		if (patron_email.contains("@sjsu.edu")) {
			Librarian librarian = new Librarian();

			librarian.setLibrarian_name(patron_name);

			librarian.setLibrarian_email(patron_email);
			librarian.setLibrarian_id(patron_id);
			patron_password = getSHADigest(patron_password);
			librarian.setLibrarian_password(patron_password);
			if (!libDao.isLibrarianPresent(librarian)) {

				libDao.createLibrarian(librarian);
				// System.out.println("created libraraian");
			} else {
				model.addAttribute("error", "This email already exist!");
				return "librariansignup";
			}

			String link = "http://1-dot-cmpe-275-term-project-team-13.appspot.com/librarianActivationlink?key=";
			String s = librarian.getLibrarian_id() + ":" + librarian.getLibrarian_email() + ":" + "shrutSalt";
			// System.out.println("string:" + s);
			link = link + Base64.encodeBase64String(s.getBytes());
			String sender = "librarymanagement275@gmail.com";// write here
																// sender gmail
																// id
			String receiver = librarian.getLibrarian_email();// write here
																// receiver id
			mail.sendMail(sender, receiver, "Sub: activation link", link);

		}

		else {
			Patron patron = new Patron();
			patron.setPatron_name(patron_name);
			patron.setPatron_email(patron_email);
			patron.setPatron_id(patron_id);
			patron_password = getSHADigest(patron_password);
			patron.setPatron_password(patron_password);

			if (!patronDAO.isPatronPresent(patron)) {

				patronDAO.createPatron(patron);
				// System.out.println("created patron");

				// mail =(Mailmail)context.getBean("mailMail");

				String link = "http://1-dot-cmpe-275-term-project-team-13.appspot.com/activationlink?key=";
				String s = patron.getPatron_id() + ":" + patron.getPatron_email() + ":" + "shrutSalt";
				System.out.println("string:" + s);
				link = link + Base64.encodeBase64String(s.getBytes());
				String sender = "librarymanagement275@gmail.com";// write here
																	// sender
																	// gmail id
				String receiver = patron.getPatron_email();// write here
															// receiver id
				mail.sendMail(sender, receiver, "Sub: activation link", link);

				System.out.println("success");
			} else {
				System.out.println("already present");
			}
		}

		return "home";
	}

	@RequestMapping(value = "/activationlink", method = RequestMethod.GET)
	public String updateActivationLink(@RequestParam("key") String key, Model model) {
		byte[] b = Base64.decodeBase64(key);
		String decodedKey = new String(b);
		if (decodedKey.contains(":")) {
			String[] s = decodedKey.split(":");
			int patron_id = Integer.parseInt(s[0]);
			String username = s[1];
			Patron patron = patronDAO.getPatron(patron_id);
			patron.setPatron_verified(true);
			patronDAO.updatePatron(patron);
		}
		return "home";
	}

	private String getSHADigest(String string) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		byte[] bytes = md.digest(string.getBytes());
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}

}
