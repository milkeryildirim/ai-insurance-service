package tech.yildirim.aiinsurance.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository)
      throws Exception {
    http.csrf(AbstractHttpConfigurer::disable) // Disable CSRF for OAuth2 flow
        .authorizeHttpRequests(
            authz ->
                authz
                    // Public endpoints (health check, static resources)
                    .requestMatchers(
                        "/actuator/health",
                        "/",
                        "/index.html",
                        "/static/**",
                        "/css/**",
                        "/js/**",
                        "/images/**")
                    .permitAll()
                    // API endpoints that don't require authentication
                    .requestMatchers("/api/public/**", "/api/test/**")
                    .permitAll()
                    // Error page
                    .requestMatchers("/error")
                    .permitAll()
                    // All other requests require authentication
                    .anyRequest()
                    .authenticated())
        .oauth2Login(
            oauth2 ->
                oauth2
                    .loginPage("/oauth2/authorization/okta")
                    .defaultSuccessUrl("/", true)
                    .failureUrl("/login?error"))
        .logout(
            logout ->
                logout
                    .logoutUrl("/logout")
                    .logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository))
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .deleteCookies("JSESSIONID"));

        return http.build();
    }

  @Bean
  public LogoutSuccessHandler oidcLogoutSuccessHandler(
      ClientRegistrationRepository clientRegistrationRepository) {
    OidcClientInitiatedLogoutSuccessHandler logoutSuccessHandler =
        new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);

    // Set the post logout redirect URI
    logoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}");

    return logoutSuccessHandler;
  }
}
