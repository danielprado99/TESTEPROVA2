package br.insper.TESTEPROVA2;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TokenService {

    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);
    private static final String VALIDATION_URL = "http://184.72.80.215/usuario/validate";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public TokenService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public boolean validateTokenAndRole(String token, String requiredRole) {
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    VALIDATION_URL,
                    HttpMethod.GET,
                    createHttpEntity(token),
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                String userRole = jsonNode.path("role").asText();
                logger.info("User role: {}, Required role: {}", userRole, requiredRole);

                if ("ADMIN".equals(userRole)) {
                    return true; // ADMIN tem acesso a tudo
                } else if ("DEVELOPER".equals(userRole) && ("DEVELOPER".equals(requiredRole) || "GET".equals(requiredRole))) {
                    return true; // DEVELOPER tem acesso apenas para operações GET
                }
            }
            logger.warn("Token validation failed or insufficient permissions");
            return false;
        } catch (Exception e) {
            logger.error("Error validating token: ", e);
            return false;
        }
    }

    private HttpEntity<String> createHttpEntity(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.replace("Bearer ", ""));
        return new HttpEntity<>(headers);
    }
}
