package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay

        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();

        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());

        // calculate total amount
        int totalAmount = 0;
        if (subscriptionEntryDto.getSubscriptionType() == SubscriptionType.BASIC){
            totalAmount = 500 + (subscriptionEntryDto.getNoOfScreensRequired() * 200);
        }
        else if (subscriptionEntryDto.getSubscriptionType() == SubscriptionType.PRO){
            totalAmount = 800 + (subscriptionEntryDto.getNoOfScreensRequired() * 250);
        }
        else{
            totalAmount = 1000 + (subscriptionEntryDto.getNoOfScreensRequired() * 350);
        }
        subscription.setTotalAmountPaid(totalAmount);
        subscription.setStartSubscriptionDate(new Date());
        subscription.setUser(user);

        user.setSubscription(subscription);

        Subscription createdSubscription = subscriptionRepository.save(subscription);

        return totalAmount;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

        User user = userRepository.findById(userId).get();
        Subscription subscription = user.getSubscription();

        int expectedTotalAmount = 0;
        int difference = 0;
        if (subscription.getSubscriptionType() == SubscriptionType.ELITE){
            throw new Exception("Already the best Subscription");
        }
        else if (subscription.getSubscriptionType() == SubscriptionType.PRO){
            expectedTotalAmount = 1000 + (subscription.getNoOfScreensSubscribed() * 350);
            difference = expectedTotalAmount - subscription.getTotalAmountPaid();
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            subscription.setTotalAmountPaid(expectedTotalAmount);
        }
        else {
            expectedTotalAmount = 800 + (subscription.getNoOfScreensSubscribed() * 250);
            difference = expectedTotalAmount - subscription.getTotalAmountPaid();
            subscription.setSubscriptionType(SubscriptionType.PRO);
            subscription.setTotalAmountPaid(expectedTotalAmount);
        }
        subscriptionRepository.save(subscription);

        return difference;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        int totalRevenue = 0;
        for (Subscription subscription : subscriptionRepository.findAll()){
            totalRevenue += subscription.getTotalAmountPaid();
        }

        return totalRevenue;
    }

}
