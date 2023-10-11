package com.zdn.zdn.client;

import com.zdn.zdn.model.BeerDTO;
import org.springframework.data.domain.Page;

public interface BeerClient  {

    Page<BeerDTO> listBeers(String beerName);

}
