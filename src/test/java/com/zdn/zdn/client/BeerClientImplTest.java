package com.zdn.zdn.client;

import com.zdn.zdn.model.BeerDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

@SpringBootTest
class BeerClientImplTest {

    @Autowired
    BeerClientImpl beerClient;

    @Test
    void listBeersWithName() {
        System.out.println(beerClient.listBeers("ALE", null, false, 1, 25));
    }

    @Test
    void listBeersWithoutBeerName() {
        beerClient.listBeers(null, null, false, 1, 25);
    }

    @Test
    void listBeersWithPageSize() {
        beerClient.listBeers(null, null, false, 1, 20);

    }

    @Test
    void getBeerById() {

        Page<BeerDTO> beerDTOS = beerClient.listBeers();
        BeerDTO beerDTO = beerDTOS.getContent().get(0);
        BeerDTO byIdBeerDTO = beerClient.getBeerById(beerDTO.getId());
        Assertions.assertNotNull(byIdBeerDTO);

    }
}