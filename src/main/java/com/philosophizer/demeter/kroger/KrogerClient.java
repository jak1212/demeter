package com.philosophizer.demeter.kroger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.philosophizer.demeter.model.KrogerItemDto;
import com.philosophizer.demeter.utils.SearchTermNormalizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class KrogerClient {

    private static final Duration EXPIRY_SKEW = Duration.ofSeconds(60);
    @Value("${kroger.auth-url}")
    private String authUrl;
    @Value("${kroger.client-id}")
    private String clientId;
    @Value("${kroger.client-secret}")
    private String clientSecret;
    @Value("${kroger.scope}")
    private String scope;
    private final RestClient restClient = RestClient.builder()
            .requestInterceptor((request, body, execution) -> {
                System.out.println(request.getMethod() + " " + request.getURI());   // breakpoint works here too
                return execution.execute(request, body);
            })
            .build();
    private String cachedToken;
    private Instant cachedTokenExpiry = Instant.EPOCH;
    @Value("${kroger.search-url}")
    private String searchUrl;
    @Value("${kroger.search-location-parameter}")
    private String searchParameter;
    private static final double MIN_COVERAGE = 0.75;
    @Autowired
    public SearchTermNormalizer normalizer;
    public record ScoredProduct(KrogerItemDto product, double coverage, int extraTokens) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record TokenResponse(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("expires_in") long expiresIn) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ProductSearchResponse(List<KrogerItemDto> data) {}

    public KrogerClient() {
    }

    public synchronized String getToken() {
        if(cachedToken != null && Instant.now().isBefore(cachedTokenExpiry.minus(EXPIRY_SKEW))) {
            return cachedToken;
        }

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("scope", scope);

        TokenResponse response = restClient.post()
                .uri(authUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .headers(h -> h.setBasicAuth(clientId, clientSecret))
                .body(form)
                .retrieve()
                .body(TokenResponse.class);

        if(response == null || response.accessToken() == null) {
            throw new IllegalStateException("Kroger token response was empty");
        }

        cachedToken = response.accessToken();
        cachedTokenExpiry = Instant.now().plusSeconds(response.expiresIn());
        return cachedToken;
    }

    public List<KrogerItemDto> getProductsForSearchTerm(String searchTerm){

        ProductSearchResponse response = restClient.get()
                .uri(searchUrl + "?filter.term={term}&filter.limit={limit}", searchTerm, 10)
                .headers(h -> h.setBearerAuth(getToken()))
                .retrieve()
                .body(ProductSearchResponse.class);

        return (response == null || response.data() == null) ? List.of() : response.data();
    }

    public Optional<ScoredProduct> pickBest(String rawLine, List<KrogerItemDto> candidates) {
        Set<String> query = normalizer.stemmedTokens(rawLine);
        if (query.isEmpty() || candidates == null) return Optional.empty();

        return candidates.stream()
                .filter(c -> c.description() != null)
                .map(c -> {
                    Set<String> desc = normalizer.stemmedTokens(c.description());
                    long hits = query.stream().filter(desc::contains).count();
                    return new ScoredProduct(c, (double) hits / query.size(), desc.size() - (int) hits);
                })
                .filter(s -> s.coverage() >= MIN_COVERAGE)
                .min(Comparator.comparingDouble((ScoredProduct s) -> -s.coverage())
                        .thenComparingInt(ScoredProduct::extraTokens));
    }




}
