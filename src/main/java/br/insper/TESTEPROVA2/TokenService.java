package br.insper.TESTEPROVA2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TokenService {

    @Value("${auth.validation.url}")
    private String validationUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public TokenService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public boolean validateTokenAndRole(String token, String requiredRole) {
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    validationUrl,
                    HttpMethod.GET,
                    createHttpEntity(token),
                    String.class
            );

            if (response.getBody() != null) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                String userRole = jsonNode.path("role").asText();
                return userRole.equals(requiredRole);
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private HttpEntity<String> createHttpEntity(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.replace("Bearer ", ""));
        return new HttpEntity<>(headers);
    }
}
