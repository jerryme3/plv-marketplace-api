package com.marketplace.plv.shared.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	@Value("${app.jwt.secret}")
	private String key;

	@Bean
	AuthenticationProvider authenticationProvider(
			UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder
	) {
		var provider = new DaoAuthenticationProvider(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder);

		return provider;
	}

	@Bean
	JwtEncoder jwtEncoder() {
		byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
		SecretKey key = new SecretKeySpec(keyBytes, "HmacSHA256");
		JWK jwk = new OctetSequenceKey.Builder(key)
				.algorithm(JWSAlgorithm.HS256)
				.build();

		JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));

		return new NimbusJwtEncoder(jwks);
	}

	@Bean
	JwtDecoder jwtDecoder() {
		var keyBytes = key.getBytes(StandardCharsets.UTF_8);
		var key = new SecretKeySpec(keyBytes, "HmacSHA256");

		return NimbusJwtDecoder.withSecretKey(key).build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) {
		http
				.csrf(AbstractHttpConfigurer::disable)
				.cors(c -> c.configurationSource(corsConfigurationSource()))
				.httpBasic(withDefaults())
				.sessionManagement(
						o -> o.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				)
				.authorizeHttpRequests(
						req -> req
								.requestMatchers(HttpMethod.GET, "/", "/error").permitAll()
								.requestMatchers("/api/v1/auth/**").permitAll()
								.requestMatchers("/api/v1/product/**").permitAll()
								.requestMatchers("/api/v1/me/update/**")
								.authenticated().anyRequest().authenticated()
				)
				.oauth2ResourceServer(oa -> oa.bearerTokenResolver(cookieBearerTokenResolver())
						.jwt(j -> jwtDecoder()));

		return http.build();
	}

	//A bean for CORS to let the frontend communicate to the backend (still won't let me connect to the backend)
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOriginPatterns(List.of("http://localhost:*", "http://127.0.0.1:*"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("*"));
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	@Bean
	BearerTokenResolver cookieBearerTokenResolver() {
		return req -> {
			if (req.getCookies() == null) return null;

			return Arrays.stream(req.getCookies())
					.filter(cookie -> "AUTH_TOKEN".equals(cookie.getName()))
					.map(Cookie::getValue)
					.findFirst()
					.orElse(null);
		};
	}
}
