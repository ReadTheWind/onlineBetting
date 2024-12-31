package com.onlinebetting.controller;

import com.onlinebetting.service.StakeService;
import org.springframework.web.bind.annotation.*;

@RestController
public class StakeController {

    private StakeService stakeService;

    public StakeController(StakeService stakeService) {
        this.stakeService = stakeService;
    }

    @PostMapping("/{betOfferId}/stake")
    public void offerStake(@PathVariable("betOfferId") long betOfferId,
                           @RequestParam(value = "sessionKey") String sessionKey,
                           @RequestBody double amount) throws Exception {
        stakeService.offerStake(betOfferId, sessionKey, amount);
    }

    @GetMapping("/{betOfferId}/highStakes")
    public String highStakes(@PathVariable("betOfferId") long betOfferId) {
        return stakeService.highStakes(betOfferId);
    }


}
