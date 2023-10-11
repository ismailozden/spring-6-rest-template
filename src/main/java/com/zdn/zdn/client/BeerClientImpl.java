package com.zdn.zdn.client;

import com.zdn.zdn.model.BeerDTO;
import com.zdn.zdn.model.BeerDTOPageImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Service
public class BeerClientImpl implements BeerClient {

    private final RestTemplateBuilder restTemplateBuilder;
    private static final String GET_BEER_PATH = "/api/v1/beer";
    @Override
    public BeerDTOPageImpl<BeerDTO> listBeers() {

        RestTemplate restTemplate = restTemplateBuilder.build();

        ParameterizedTypeReference<BeerDTOPageImpl<BeerDTO>> responseType = new ParameterizedTypeReference<BeerDTOPageImpl<BeerDTO>>() {};
        ResponseEntity<BeerDTOPageImpl<BeerDTO>> response = restTemplate
                .exchange(GET_BEER_PATH, HttpMethod.GET, null, responseType);


        return response.getBody();
    }
}
