package com.project.flashcardApp.services;

import java.time.LocalDateTime;
import java.util.Random;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.project.flashcardApp.entities.EmailConfirmationToken;
import com.project.flashcardApp.entities.ForgottenPasswordChangeToken;
import com.project.flashcardApp.entities.RefreshToken;
import com.project.flashcardApp.entities.User;
import com.project.flashcardApp.exceptions.EmailAlreadyConfirmedException;
import com.project.flashcardApp.exceptions.EmailAlreadyInUseException;
import com.project.flashcardApp.exceptions.EmailInvalidException;
import com.project.flashcardApp.exceptions.InvalidPasswordException;
import com.project.flashcardApp.exceptions.PasswordAlreadyConfirmedException;
import com.project.flashcardApp.exceptions.RefreshTokenInvalidException;
import com.project.flashcardApp.exceptions.RefreshTokenValidException;
import com.project.flashcardApp.exceptions.ConfirmationTokenExpiredException;
import com.project.flashcardApp.exceptions.UsernameAlreadyInUseException;
import com.project.flashcardApp.requests.RefreshTokenRequest;
import com.project.flashcardApp.requests.UserChangeForgottenPasswordRequest;
import com.project.flashcardApp.requests.UserForgotPasswordRequest;
import com.project.flashcardApp.requests.UserLoginRequest;
import com.project.flashcardApp.requests.UserRegisterConfirmRequest;
import com.project.flashcardApp.requests.UserRegisterRequest;
import com.project.flashcardApp.responses.AuthResponse;
import com.project.flashcardApp.security.JwtTokenProvider;
import com.project.flashcardApp.utilities.registration.EmailSender;
import com.project.flashcardApp.utilities.registration.EmailValidator;

@Service
public class AuthService {

	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;
	private final PasswordEncoder passwordEncoder;
	private final UserService userService;
	private final RefreshTokenService refreshTokenService;
	private final EmailValidator emailValidator;
	private final EmailConfirmationTokenService emailConfirmationTokenService;
	private final ForgottenPasswordChangeTokenService forgottenPasswordChangeTokenService;
	private final EmailSender emailSender;
	
	public AuthService(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider,
			PasswordEncoder passwordEncoder, UserService userService, RefreshTokenService refreshTokenService,
			EmailValidator emailValidator, EmailConfirmationTokenService emailConfirmationTokenService, ForgottenPasswordChangeTokenService 
			forgottenPasswordChangeTokenService, EmailSender emailSender) {
		this.authenticationManager = authenticationManager;
		this.jwtTokenProvider = jwtTokenProvider;
		this.passwordEncoder = passwordEncoder;
		this.userService = userService;
		this.refreshTokenService = refreshTokenService;
		this.emailValidator = emailValidator;
		this.emailConfirmationTokenService = emailConfirmationTokenService;
		this.forgottenPasswordChangeTokenService = forgottenPasswordChangeTokenService;
		this.emailSender = emailSender;
	}

	public AuthResponse login(UserLoginRequest loginRequest) {
		User user = userService.getOneUserByUsername(loginRequest.getUsername());

		if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
			throw new InvalidPasswordException("Wrong password.");
		}

		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
				loginRequest.getUsername(), loginRequest.getPassword());
		Authentication auth = authenticationManager.authenticate(authToken);
		SecurityContextHolder.getContext().setAuthentication(auth);
		String jwtToken = jwtTokenProvider.generateJwtToken(auth);
		
		AuthResponse authResponse = new AuthResponse("Bearer " + jwtToken, refreshTokenService.getByUserId(user.getId()).getToken(),
				"Successfully logged in.", user.getId());
		return authResponse;
	}

	public AuthResponse forgotPassword(UserForgotPasswordRequest userForgotPasswordRequest) {
		User user = userService.getOneUserByEmail(userForgotPasswordRequest.getEmail());
		
		String token = getRandomNumberString();
		ForgottenPasswordChangeToken confirmationToken = new ForgottenPasswordChangeToken(user, token, LocalDateTime.now(),
				LocalDateTime.now().plusMinutes(15));
		forgottenPasswordChangeTokenService.saveConfirmationToken(confirmationToken);
		
		emailSender.send(user.getEmail(), buildForgotPasswordEmail(user.getUsername(), token), "Change your password");
		AuthResponse authResponse = new AuthResponse();
		authResponse.setMessage("Change password code sent to your email.");
		return authResponse;
	}
	
	public AuthResponse changePassword(UserChangeForgottenPasswordRequest userChangeForgottenPasswordRequest) {		
		String token = userChangeForgottenPasswordRequest.getToken();
		ForgottenPasswordChangeToken confirmationToken = forgottenPasswordChangeTokenService.getConfirmationToken(token);
		
		if (confirmationToken.getConfirmedAt() != null) {
			throw new PasswordAlreadyConfirmedException("Password already changed.");
		}
		
		if(forgottenPasswordChangeTokenService.isTokenExpired(token)) {
			forgottenPasswordChangeTokenService.deleteConfirmationToken(token);
			throw new ConfirmationTokenExpiredException("Token expired.");
		}
		
		User user = userService.getOneUserByEmail(userChangeForgottenPasswordRequest.getEmail());
		userService.changeForgottenPassword(user.getEmail(), userChangeForgottenPasswordRequest);
		
		forgottenPasswordChangeTokenService.setTokenConfirmedAt(token);
			
		AuthResponse authResponse = new AuthResponse();
		authResponse.setMessage("Password successfully changed.");
		return authResponse;
		
	}

	public AuthResponse register(UserRegisterRequest registerRequest) {
		if (userService.userExistsByEmail(registerRequest.getEmail())) {
			throw new EmailAlreadyInUseException("Email already in use.");
		}
		if (userService.userExistsByUsername(registerRequest.getUsername())) {
			throw new UsernameAlreadyInUseException("Username already in use.");
		}
		if (!emailValidator.test(registerRequest.getEmail())) {
			throw new EmailInvalidException("Email is invalid.");
		}
		
		User user = new User(registerRequest.getUsername(), registerRequest.getEmail(), false, passwordEncoder.encode(registerRequest.getPassword()), 0);
		userService.saveOneUser(user);

		// Email confirmation token
		String token = getRandomNumberString();
		EmailConfirmationToken confirmationToken = new EmailConfirmationToken(user, token, LocalDateTime.now(),
				LocalDateTime.now().plusMinutes(15));
		emailConfirmationTokenService.saveConfirmationToken(confirmationToken);

		emailSender.send(registerRequest.getEmail(), buildEmail(registerRequest.getUsername(), token), "Confirm your email");
		AuthResponse authResponse = new AuthResponse();
		authResponse.setMessage("Email confirmation code is sent.");
		return authResponse;
	}
	
	private String getRandomNumberString() {
	    Random rnd = new Random();
	    return String.format("%06d", rnd.nextInt(999999));
	}
	
	public AuthResponse confirmEmailToken(UserRegisterConfirmRequest userRegisterConfirmRequest) {
		String token = userRegisterConfirmRequest.getToken();
		EmailConfirmationToken confirmationToken = emailConfirmationTokenService.getConfirmationToken(token);
		
		if (confirmationToken.getConfirmedAt() != null) {
			throw new EmailAlreadyConfirmedException("Email already confirmed");
		}
		
		if(emailConfirmationTokenService.isTokenExpired(token)) {
			userService.deleteOneUserById(confirmationToken.getUser().getId());
			emailConfirmationTokenService.deleteConfirmationToken(token);
			throw new ConfirmationTokenExpiredException("Token expired.");
		}
		
		emailConfirmationTokenService.setTokenConfirmedAt(token);
		User user = confirmationToken.getUser();
		userService.enableUser(user.getEmail());

		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
				user.getUsername(), userRegisterConfirmRequest.getPassword());
		Authentication auth = authenticationManager.authenticate(authToken);
		SecurityContextHolder.getContext().setAuthentication(auth);
		String jwtToken = jwtTokenProvider.generateJwtToken(auth);
			
		AuthResponse authResponse = new AuthResponse();
		authResponse.setMessage("Successfully registered.");
		authResponse.setJwtAccessToken("Bearer " + jwtToken);
		authResponse.setUserId(user.getId());
		authResponse.setJwtRefreshToken(refreshTokenService.createRefreshToken(user));
		return authResponse;
	}

	public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
		RefreshToken refreshToken = refreshTokenService.getByUserId(refreshTokenRequest.getUserId());
		boolean isRefreshTokenExpired = refreshTokenService.isRefreshTokenExpired(refreshToken);
		if (refreshToken.getToken().equals(refreshTokenRequest.getRefreshToken())
				&& isRefreshTokenExpired) {
			User user = refreshToken.getUser();
			String jwtToken = jwtTokenProvider.generateJwtTokenFromRefreshByUserId(user.getId());

			AuthResponse authResponse = new AuthResponse();
			authResponse.setMessage("Refresh token updated.");
			authResponse.setJwtAccessToken("Bearer " + jwtToken);
			authResponse.setUserId(user.getId());
			authResponse.setJwtRefreshToken(refreshTokenService.createRefreshToken(user));
			return authResponse;
		} else if (refreshToken.getToken().equals(refreshTokenRequest.getRefreshToken())
				&& !isRefreshTokenExpired) {
			throw new RefreshTokenValidException("Refresh token is valid.");
		} else {
			throw new RefreshTokenInvalidException("Refresh token is not valid.");
		}
	}
	
	public AuthResponse accessToken(Long userId) {
			String jwtToken = jwtTokenProvider.generateJwtTokenFromRefreshByUserId(userId);
			AuthResponse authResponse = new AuthResponse();
			authResponse.setMessage("Access token updated.");
			authResponse.setJwtAccessToken("Bearer " + jwtToken);
			authResponse.setUserId(userId);
			authResponse.setJwtRefreshToken(refreshTokenService.getByUserId(userId).getToken());
			return authResponse;
	}
	
    private String buildEmail(String name, String token) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please send your confirmation code to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> " + token + " </p></blockquote>\n Code will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }
    
    private String buildForgotPasswordEmail(String name, String token) {
                return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Change your password</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> You can change your password with this token : </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> " + token + " </p></blockquote>\n Code will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }

}
