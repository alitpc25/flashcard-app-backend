package com.project.flashcardApp.security;

import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtTokenProvider {

	private String APP_SECRET = "flashcardApp";

	// milliseconds
	private long EXPIRES_IN = 10800000L;

	public String generateJwtToken(Authentication auth) {
		JwtUserDetails userDetails = (JwtUserDetails) auth.getPrincipal();
		Date expireDate = new Date(new Date().getTime() + EXPIRES_IN);
		return Jwts.builder().setSubject(Long.toString(userDetails.getId())).setIssuedAt(new Date())
				.setExpiration(expireDate).signWith(SignatureAlgorithm.HS512, APP_SECRET).compact();
	}
	
	public String generateJwtTokenFromRefreshByUserId(Long userId) {
		Date expireDate = new Date(new Date().getTime() + EXPIRES_IN);
		return Jwts.builder().setSubject(Long.toString(userId)).setIssuedAt(new Date())
				.setExpiration(expireDate).signWith(SignatureAlgorithm.HS512, APP_SECRET).compact();
	}

	Long getUserIdFromJwt(String token) {
		Claims claims = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(token).getBody();
		return Long.parseLong(claims.getSubject());
	}

	boolean validateToken(String token) {
		try {
			// Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(token); // Not necessary
			return !isTokenExpired(token);
		} catch (SignatureException e) {
			return false;
		} catch (MalformedJwtException e) {
			return false;
		} catch (ExpiredJwtException e) {
			return false;
		} catch (UnsupportedJwtException e) {
			return false;
		} catch (IllegalArgumentException e) {
			return false;
		}

	}
	
	private Jws<Claims> parseTokenClaims(String token) {
		return Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(token);
	}

	private boolean isTokenExpired(String token) {
		Date expiration = parseTokenClaims(token).getBody().getExpiration();
		return expiration.before(new Date());
	}

}
