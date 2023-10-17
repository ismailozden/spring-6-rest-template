package com.zdn.zdn.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zdn.zdn.config.OAuthClientInterceptor;
import com.zdn.zdn.config.RestTemplateBuilderConfig;
import com.zdn.zdn.model.BeerDTO;
import com.zdn.zdn.model.BeerDTOPageImpl;
import com.zdn.zdn.model.BeerStyle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.MockServerRestTemplateCustomizer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest
@Import(RestTemplateBuilderConfig.class)
public class BeerClientMockTest {

    static final String URL = "http://localhost:8080";
    public static final String BEARER_TOKEN = "Bearer test-token";

    BeerClient beerClient;

    @Autowired
    RestTemplateBuilder restTemplateBuilder;

    MockRestServiceServer server;

    @Autowired
    ObjectMapper objectMapper;

    @Mock
    RestTemplateBuilder mockRestTemplateBuilder = new RestTemplateBuilder(new MockServerRestTemplateCustomizer());

    BeerDTO beerDTO;
    String dtoJson;

    @MockBean
    OAuth2AuthorizedClientManager manager;

    @TestConfiguration
    public static class TestConfig{

        @Bean
        ClientRegistrationRepository clientRegistrationRepository(){
            return new InMemoryClientRegistrationRepository(ClientRegistration
                    .withRegistrationId("springauth")
                    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                    .clientId("test-client")
                    .tokenUri("test-uri")
                    .build());
        }
        @Bean
        OAuth2AuthorizedClientService oAuth2AuthorizedClientService(ClientRegistrationRepository clientRegistrationRepository){
            return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
        }

        @Bean
        OAuthClientInterceptor oAuthClientInterceptor(OAuth2AuthorizedClientManager manager,ClientRegistrationRepository clientRegistrationRepository){
            return new OAuthClientInterceptor(manager,clientRegistrationRepository);
        }
    }

    @Autowired
    ClientRegistrationRepository clientRegistrationRepository;
    @BeforeEach
    void setUp() throws JsonProcessingException {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("springauth");
        OAuth2AccessToken token = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
                "test-token", Instant.MIN, Instant.MAX);
        when(manager.authorize(any())).thenReturn(new OAuth2AuthorizedClient(clientRegistration, "test-client", token));
        RestTemplate restTemplate = restTemplateBuilder.build();
        server = MockRestServiceServer.bindTo(restTemplate).build();
        when(mockRestTemplateBuilder.build()).thenReturn(restTemplate);
        beerClient = new BeerClientImpl(mockRestTemplateBuilder);
        beerDTO = getBeerDto();
        dtoJson = objectMapper.writeValueAsString(beerDTO);
    }

    @Test
    void listBeers() throws JsonProcessingException {
        String payload = objectMapper.writeValueAsString(getPage());
        server.expect(method(HttpMethod.GET))
                .andExpect(requestTo(URL + BeerClientImpl.GET_BEER_PATH))
                .andExpect(header("Authorization",BEARER_TOKEN))
                .andRespond(withSuccess(payload, MediaType.APPLICATION_JSON));

        Page<BeerDTO> dtos = beerClient.listBeers();
        Assertions.assertFalse(dtos.getContent().isEmpty());
    }

    BeerDTO getBeerDto(){
        return BeerDTO.builder()
                .id(UUID.randomUUID())
                .price(new BigDecimal("10.99"))
                .beerName("Mango Bobs")
                .beerStyle(BeerStyle.IPA)
                .quantityOnHand(500)
                .upc("123245")
                .build();
    }

    BeerDTOPageImpl<BeerDTO> getPage(){
        return new BeerDTOPageImpl<>(Collections.singletonList(getBeerDto()), 1, 25, 1);
    }

    @Test
    void getBeerById() {
        mockGetOperation();
        BeerDTO responseDto = beerClient.getBeerById(beerDTO.getId());
        Assertions.assertEquals(beerDTO.getId(),responseDto.getId());
    }

    private void mockGetOperation() {
        server.expect(method(HttpMethod.GET))
                .andExpect(requestToUriTemplate(URL+BeerClientImpl.GET_BEER_BY_ID_PATH, beerDTO.getId()))
                .andExpect(header("Authorization",BEARER_TOKEN))
                .andRespond(withSuccess(dtoJson, MediaType.APPLICATION_JSON));
    }

    @Test
    void createBeer() {
        URI uri = UriComponentsBuilder.fromPath(BeerClientImpl.GET_BEER_BY_ID_PATH)
                        .build(beerDTO.getId());

        server.expect(method(HttpMethod.POST))
                        .andExpect(requestTo(URL+BeerClientImpl.GET_BEER_PATH))
                                .andRespond(withAccepted().location(uri));

        mockGetOperation();
        BeerDTO responseDto = beerClient.createBeer(beerDTO);
        Assertions.assertEquals(beerDTO.getId(),responseDto.getId());
    }

    @Test
    void updateBeer() {
        server.expect(method(HttpMethod.PUT))
                .andExpect(requestToUriTemplate(URL+BeerClientImpl.GET_BEER_BY_ID_PATH, beerDTO.getId()))
                .andExpect(header("Authorization",BEARER_TOKEN))
                .andRespond(withNoContent());

        mockGetOperation();
        BeerDTO beerDTOResponse = beerClient.updateBeer(beerDTO);
        Assertions.assertEquals(beerDTO.getId(), beerDTOResponse.getId());
    }

    @Test
    void deleteBeer() {
        server.expect(method(HttpMethod.DELETE))
                .andExpect(requestToUriTemplate(URL+BeerClientImpl.GET_BEER_BY_ID_PATH, beerDTO.getId()))
                .andExpect(header("Authorization",BEARER_TOKEN))
                .andRespond(withNoContent());

        beerClient.deleteBeer(beerDTO.getId());
        server.verify();
    }

    @Test
    void deleteBeerNotFound() {
        server.expect(method(HttpMethod.DELETE))
                .andExpect(requestToUriTemplate(URL+BeerClientImpl.GET_BEER_BY_ID_PATH, beerDTO.getId()))
                .andExpect(header("Authorization",BEARER_TOKEN))
                .andRespond(withResourceNotFound());
        Assertions.assertThrows(HttpClientErrorException.class, () -> {
            beerClient.deleteBeer(beerDTO.getId());
        });
        server.verify();
    }

    @Test
    void listBeerWithQueryParam() throws JsonProcessingException {
        String response = objectMapper.writeValueAsString(getPage());
        URI uri = UriComponentsBuilder.fromHttpUrl(URL+BeerClientImpl.GET_BEER_PATH)
                .queryParam("beerName", "ALE")
                .build().toUri();

        server.expect(method(HttpMethod.GET))
                .andExpect(requestTo(uri))
                .andExpect(queryParam("beerName", "ALE"))
                .andExpect(header("Authorization",BEARER_TOKEN))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));

        Page<BeerDTO> responsePage = beerClient.listBeers("ALE",null,null,null,null);

        Assertions.assertEquals(responsePage.getContent().size(),1);
    }
}
