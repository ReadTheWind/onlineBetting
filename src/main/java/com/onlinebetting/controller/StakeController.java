package com.onlinebetting.controller;

import com.onlinebetting.service.StakeService;
import com.onlinebetting.web.annotation.*;
import com.onlinebetting.web.enums.MethodType;

@WebController(name = "stakeController")
public class StakeController {

    @Autowired
    private StakeService stakeService;

    @RequestMapping(path = "/{betOfferId}/stake", method = MethodType.POST)
    public void offerStake(@PathParameter(name = "betOfferId") long betOfferId,
                           @RequestParam(name = "sessionKey") String sessionKey,
                           @RequestBody double amount) throws Exception {
        stakeService.offerStake(betOfferId, sessionKey, amount);
    }

    @RequestMapping(path = "/{betOfferId}/highStakes", method = MethodType.GET)
    public String highStakes(@PathParameter(name = "betOfferId") long betOfferId) {
        return stakeService.highStakes(betOfferId);
    }


}
