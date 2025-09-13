package tech.yildirim.aiinsurance.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class AuthController {

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal OidcUser principal) {
        if (principal != null) {
            model.addAttribute("name", principal.getFullName());
            model.addAttribute("email", principal.getEmail());
            model.addAttribute("authenticated", true);
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
