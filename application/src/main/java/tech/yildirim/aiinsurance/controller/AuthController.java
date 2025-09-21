package tech.yildirim.aiinsurance.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import tech.yildirim.aiinsurance.service.ChatService;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

  private final ChatService chatService;

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal OidcUser principal) {
        if (principal != null) {
            model.addAttribute("name", principal.getFullName());
            model.addAttribute("email", principal.getEmail());
            model.addAttribute("authenticated", true);

      // Generate personalized welcome message using AI
      try {
        String welcomeMessage = chatService.generateWelcomeMessage(principal);
        model.addAttribute("welcomeMessage", welcomeMessage);
        log.debug("Generated welcome message for user: {}", principal.getEmail());
      } catch (Exception e) {
        log.error("Failed to generate welcome message for user: {}", principal.getEmail(), e);
        // Fallback welcome message
        model.addAttribute(
            "welcomeMessage",
            "Hello! I'm Martin, your AI insurance assistant. How can I help you today?");
      }
        } else {
            model.addAttribute("authenticated", false);
        }
        return "index";
    }

    @GetMapping("/profile")
    @ResponseBody
    public Map<String, Object> profile(@AuthenticationPrincipal OidcUser principal) {
        if (principal != null) {
            return principal.getClaims();
        }
        return Map.of("error", "Not authenticated");
    }

    @GetMapping("/login")
    public String login() {
        return "redirect:/oauth2/authorization/okta";
    }

    @GetMapping("/login/oauth2/code/okta")
    public String loginCallback() {
        // This endpoint handles the callback from Okta after authentication
        return "redirect:/";
    }
}
