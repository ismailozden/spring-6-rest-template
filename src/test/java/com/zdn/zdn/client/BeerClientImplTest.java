package com.zdn.zdn.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
}