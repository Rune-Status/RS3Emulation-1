package com.rs3e.utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;

public class Encryption {

	/**
	 * The encryption
	 */
	private final String encryption;

	/**
	 * The date
	 */
	private final Date date;

	/**
	 * The algorithm used to encrypt
	 */
	private final String algorithm;

	public Encryption(String encryption, String algorithm, Date date) {
		this.encryption = encryption;
		this.date = date;
		this.algorithm = algorithm;
	}

	/**
	 * Encrypts a buffer using MD5 algorithm
	 * 
	 * @param buffer
	 *            The buffer to encrypt
	 * @return The encrypted buffer
	 */
	public static final byte[] MD5Encryption(byte[] buffer) {
		try {
			MessageDigest MD5 = MessageDigest.getInstance("MD5");
			MD5.update(buffer);
			byte[] digest = MD5.digest();
			MD5.reset();
			return digest;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Encrypts a {@link String} using SHA-1 algorithm
	 * 
	 * @param string
	 *            The {@link String} to encrypt
	 * @return The encryption
	 */
	public static Encryption SHA1Encryption(String string) {
		byte[] encryptionType = digest(string, "SHA-1");
		StringBuffer s = new StringBuffer(encryptionType.length * 2);
		for (int i = 0; i < encryptionType.length;) {
			int nameIndex = encryptionType[i] & 0xff;
			if (nameIndex < 16) {
				s.append('0');
			}
			s.append(Integer.toHexString(nameIndex));
			return new Encryption(s.toString(), "SHA-1", Calendar.getInstance()
					.getTime());
		}
		return null;
	}

	/**
	 * Returns a message digest
	 * 
	 * @param message
	 *            The message
	 * @param algorithm
	 *            The algorithm
	 * @return The digest buffer
	 */
	public static byte[] digest(String message, String algorithm) {
		MessageDigest string = null;
		try {
			string = MessageDigest.getInstance(algorithm);
			string.reset();
			string.update(message.getBytes());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return string.digest();
	}

	/**
	 * Returns the encryption
	 * 
	 * @return The string encryption
	 */
	public String getEncryption() {
		return encryption;
	}

	/**
	 * Returns the date
	 * 
	 * @return
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Returns the algorithm
	 * 
	 * @return The algorithm
	 */
	public String getAlgorithm() {
		return algorithm;
	}

}
