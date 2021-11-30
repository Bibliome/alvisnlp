package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.library;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Function;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.FunctionLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Library;

@Library("hash")
public abstract class HashLibrary extends FunctionLibrary {
	public static final String NAME = "hash";

	private static final String hash(String algo, String s) {
		try {
			MessageDigest md = MessageDigest.getInstance(algo);
			md.update(s.getBytes("UTF-8"));
			byte[] digest = md.digest();
			return DatatypeConverter.printHexBinary(digest);
		}
		catch (NoSuchAlgorithmException|UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Function
	public static final String md2(String s) {
		return hash("MD2", s);
	}
	
	@Function
	public static final String md5(String s) {
		return hash("MD5", s);
	}
	
	@Function(firstFtor = "sha-1")
	public static final String sha1(String s) {
		return hash("SHA-1", s);
	}
	
	@Function(firstFtor = "sha-256")
	public static final String sha256(String s) {
		return hash("SHA-256", s);
	}
	
	@Function(firstFtor = "sha-512")
	public static final String sha512(String s) {
		return hash("SHA-512", s);
	}
}
