package com.zdn.zdn.client;

import com.zdn.zdn.model.BeerDTO;
import com.zdn.zdn.model.BeerDTOPageImpl;
import com.zdn.zdn.model.BeerStyle;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BeerClientImpl implements BeerClient {

    private final RestTemplateBuilder restTemplateBuilder;
    public static final String GET_BEER_PATH = "/api/v1/beer";
    public static final String GET_BEER_BY_ID_PATH = "/api/v1/beer/{beerId}";

    @Override
    public Page<BeerDTO> listBeers() {
        return this.listBeers(null,null,null,null,null);
    }

    @Override
    public BeerDTOPageImpl<BeerDTO> listBeers(String beerName, BeerStyle beerStyle, Boolean showInventory, Integer pageNumber, Integer pageSize) {

        RestTemplate restTemplate = restTemplateBuilder.build();

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath(GET_BEER_PATH);

        if (beerName != null){
            uriComponentsBuilder.queryParam("beerName", beerName);
        }

        if (beerStyle != null){
            uriComponentsBuilder.queryParam("beerStyle", beerStyle);
        }

        if (showInventory != null){
            uriComponentsBuilder.queryParam("showInventory", showInventory);
        }

        if (pageNumber != null){
            uriComponentsBuilder.queryParam("pageNumber", pageNumber);
        }

        if (pageSize != null){
            uriComponentsBuilder.queryParam("pageSize", pageSize);
        }


        ParameterizedTypeReference<BeerDTOPageImpl<BeerDTO>> responseType = new ParameterizedTypeReference<BeerDTOPageImpl<BeerDTO>>() {};
        ResponseEntity<BeerDTOPageImpl<BeerDTO>> response = restTemplate
                .exchange(uriComponentsBuilder.toUriString(), HttpMethod.GET, null, responseType);


        return response.getBody();
    }

    @Override
    public BeerDTO getBeerById(UUID beerId) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        return restTemplate.getForObject(GET_BEER_BY_ID_PATH,BeerDTO.class, beerId);
    }

    @Override
    public BeerDTO createBeer(BeerDTO beerDTO) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        URI uri = restTemplate.postForLocation(GET_BEER_PATH, beerDTO);
        assert uri != null;
        return restTemplate.getForObject(uri.getPath(), BeerDTO.class);
    }

    @Override
    public BeerDTO updateBeer(BeerDTO newBeerDTO) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        restTemplate.put(GET_BEER_BY_ID_PATH,newBeerDTO, newBeerDTO.getId());
        return  getBeerById(newBeerDTO.getId());
    }

    @Override
    public void deleteBeer(UUID beerId) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        restTemplate.delete(GET_BEER_BY_ID_PATH,beerId);
    }
}
