package tech.yildirim.aiinsurance.config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Base test class for integration tests that automatically excludes OAuth2 and Okta configurations.
 * All integration tests should extend this class to avoid OAuth2-related issues during testing.
 */
@SpringBootTest(
    properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration," +
        "org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration," +
        "com.okta.spring.boot.oauth.OktaOAuth2AutoConfig"
    }
)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {
    // Base class for integration tests with OAuth2 disabled
}
