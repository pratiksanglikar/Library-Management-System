package edu.cmpe275.team13.beans;

/**
 * CREATE TABLE verify_email (
 * 		user_email VARCHAR(50), 
 * 		verification_key VARCHAR(65), 
 * 		PRIMARY KEY(user_email));
 */
public class EmailVerification {
	private String user_email;
	private String verification_key;
	
	public EmailVerification() {
		this.user_email = null;
		this.verification_key = null;
	}
	
	public EmailVerification(String email, String verification_key) {
		this.user_email = email;
		this.verification_key = verification_key;
	}

	/**
	 * @return the user_email
	 */
	public String getUser_email() {
		return user_email;
	}

	/**
	 * @param user_email the user_email to set
	 */
	public void setUser_email(String user_email) {
		this.user_email = user_email;
	}

	/**
	 * @return the verification_key
	 */
	public String getVerification_key() {
		return verification_key;
	}

	/**
	 * @param verification_key the verification_key to set
	 */
	public void setVerification_key(String verification_key) {
		this.verification_key = verification_key;
	}
}
