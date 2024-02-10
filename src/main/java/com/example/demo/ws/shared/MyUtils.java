package com.example.demo.ws.shared;

import com.example.demo.Security.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

@Component
public class MyUtils {

	private final Random RANDOM = new SecureRandom();
	private final String ALPHABET="0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private final int ITERATIONS = 10000;
	private final int KEY_LENGTH = 256;


	public String generateUserId(int length) {
		return generateRandomString(length);
	}

	public String generateAddressId(int length) {
		return generateRandomString(length);
	}

	private String generateRandomString(int length) {
		StringBuilder returnValue = new StringBuilder(length);

		for(int i =0 ;i<length;i++) {
			returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
		}
		return new String(returnValue);
	}

	public static boolean hasTokenExpired(String token) {
		Claims claims = Jwts
				.parser()
				.setSigningKey(SecurityConstants.getTokenSecret())
				.build()
				.parseSignedClaims(token)
				.getPayload();

		Date tokenExpire = claims.getExpiration();
		Date todayDate = new Date();
		return tokenExpire.before(todayDate);
	}

	public String generateEmailVerificationTOken(String publicUserId) {
		String token = Jwts.builder()
				.subject(publicUserId)
				.expiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS512,SecurityConstants.getTokenSecret())
				.compact();
		return token;
	}
}
