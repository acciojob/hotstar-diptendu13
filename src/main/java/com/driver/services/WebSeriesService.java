package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo

        WebSeries existingWebSeries = webSeriesRepository.findBySeriesName(webSeriesEntryDto.getSeriesName());
        if (existingWebSeries != null){
            if (existingWebSeries.getSeriesName().equalsIgnoreCase(webSeriesEntryDto.getSeriesName())){
                throw new Exception("Series is already present");
            }
        }
        // create new webseries object
        WebSeries webSeries = new WebSeries();

        webSeries.setSeriesName(webSeriesEntryDto.getSeriesName());
        webSeries.setAgeLimit(webSeriesEntryDto.getAgeLimit());
        webSeries.setRating(webSeriesEntryDto.getRating());
        webSeries.setSubscriptionType(webSeriesEntryDto.getSubscriptionType());

        ProductionHouse productionHouse = productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId()).get();

        webSeries.setProductionHouse(productionHouse);
        productionHouse.getWebSeriesList().add(webSeries);

        WebSeries addedWebseries = webSeriesRepository.save(webSeries);

        double totalPoints = 0.0;
        for (WebSeries webSeries1 : productionHouse.getWebSeriesList()){
            totalPoints += webSeries1.getRating();
        }
        double avgRating = totalPoints / productionHouse.getWebSeriesList().size();
        double precisionAvgRating = Math.round(avgRating * 100) / 100.0;

        productionHouse.setRatings(precisionAvgRating);

        productionHouseRepository.save(productionHouse);
        
        return addedWebseries.getId();
    }

}
