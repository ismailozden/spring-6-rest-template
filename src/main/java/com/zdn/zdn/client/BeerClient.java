package com.zdn.zdn.client;

import com.zdn.zdn.model.BeerDTO;
import com.zdn.zdn.model.BeerStyle;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface BeerClient  {

    Page<BeerDTO> listBeers();
    Page<BeerDTO> listBeers(String beerName, BeerStyle beerStyle, Boolean showInventory, Integer pageNumber, Integer pageSize);

    BeerDTO getBeerById(UUID beerId);
}
