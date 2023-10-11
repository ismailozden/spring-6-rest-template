package com.zdn.zdn.client;

import com.zdn.zdn.model.BeerDTO;
import com.zdn.zdn.model.BeerStyle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

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

    @Test
    void createBeer() {
        BeerDTO beerDTO = BeerDTO.builder()
                .price(new BigDecimal("9.99"))
                .beerName("Do not Drink this")
                .beerStyle(BeerStyle.GOSE)
                .quantityOnHand(800)
                .upc("4554555")
                .build();

        BeerDTO savedDTO = beerClient.createBeer(beerDTO);
        Assertions.assertNotNull(savedDTO);
    }

    @Test
    void updateBeer() {
        BeerDTO beerDTO = BeerDTO.builder()
                .price(new BigDecimal("9.99"))
                .beerName("Do not Drink this, Please")
                .beerStyle(BeerStyle.GOSE)
                .quantityOnHand(800)
                .upc("4554555")
                .build();

        BeerDTO newBeerDTO = beerClient.createBeer(beerDTO);
        final String newName = "Hey hey beer";
        newBeerDTO.setBeerName(newName);
        BeerDTO updatedBeer = beerClient.updateBeer(newBeerDTO);

        Assertions.assertEquals(newName,updatedBeer.getBeerName());

    }
}