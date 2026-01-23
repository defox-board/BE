package devfox.board.config;

import devfox.board.security.jwt.JwtUtil;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@TestConfiguration
public class TestExternalConfig {


        //소셜
    @Bean
    ClientRegistrationRepository clientRegistrationRepository() {
        return Mockito.mock(ClientRegistrationRepository.class);
    }

    @Bean
    JwtUtil jwtUtil() {
        return Mockito.mock(JwtUtil.class);
    }
}
