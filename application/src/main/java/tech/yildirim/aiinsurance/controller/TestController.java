package tech.yildirim.aiinsurance.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Test controller to verify environment configuration
 */
@RestController
public class TestController {

    @Value("${OKTA_CLIENT_ID:NOT_SET}")
    private String clientId;

    @Value("${OKTA_ISSUER:NOT_SET}")
    private String issuer;

    /**
     * Test endpoint to verify Okta configuration is loaded
     * Note: This should be removed in production for security
     */
    @GetMapping("/api/test/config")
    public Map<String, Object> testConfig() {
        return Map.of(
            "clientId", clientId.substring(0, Math.min(8, clientId.length())) + "...", // Show only first 8 chars
            "issuer", issuer,
            "redirectUri", "http://localhost:8081/login/oauth2/code/okta"
        );
    }
}
