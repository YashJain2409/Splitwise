package com.splitwise.security;
import com.splitwise.dto.UserDTO;
import com.splitwise.model.User;
import com.splitwise.model.UserAuth;
import com.splitwise.repository.UserAuthRepository;
import com.splitwise.repository.UserRepository;
import com.splitwise.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final OAuth2AuthorizedClientService clientService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    UserAuthRepository userAuthRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public CustomOAuth2SuccessHandler(OAuth2AuthorizedClientService clientService) {
        this.clientService = clientService;
    }

    @Autowired
    JWTUtil jwtUtil;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(
                authToken.getAuthorizedClientRegistrationId(), authToken.getName());
        Map<String, Object> attributes = authToken.getPrincipal().getAttributes();

        String email = (String) attributes.get("email");


        if (client != null) {
            String idToken = null;

            try{
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            }
            catch (UsernameNotFoundException e){
                    User user = new User();
                    user.setEmail(email);

                    user = userRepository.save(user);
                    UserAuth userAuth = new UserAuth();
                    userAuth.setUser(user);
                    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                    userAuth.setPassword(passwordEncoder.encode(email));
                    userAuthRepository.save(userAuth);
            }



            if (authToken.getPrincipal() instanceof OidcUser) {
                OidcUser oidcUser = (OidcUser) authToken.getPrincipal();
                idToken = jwtUtil.generateToken(email,15);
            }
            // Send the access token in the response (JSON)
            response.setContentType("application/json");
            response.getWriter().write("{ \"id_token\": \"" + idToken + "\" }");
            response.getWriter().flush();
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization failed");
        }
    }
}
