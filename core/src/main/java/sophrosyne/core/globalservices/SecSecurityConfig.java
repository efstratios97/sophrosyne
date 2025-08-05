package sophrosyne.core.globalservices;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

@EnableWebSecurity
@Configuration
public class SecSecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable);
    http.cors(
        httpSecurityCorsConfigurer -> {
          httpSecurityCorsConfigurer.configurationSource(
              request -> {
                CorsConfiguration corsConfiguration = new CorsConfiguration();
                corsConfiguration.addAllowedOriginPattern("http://*");
                corsConfiguration.addAllowedOriginPattern("https://*");
                corsConfiguration.addAllowedHeader("*");
                corsConfiguration.addAllowedMethod("*");
                corsConfiguration.addExposedHeader("*");
                corsConfiguration.setAllowCredentials(true);
                return corsConfiguration;
              });
        });
    return http.build();
  }
}
