package com.onlinebetting.service.impl;

import com.onlinebetting.constant.Constant;
import com.onlinebetting.pojo.vo.BetOffer;
import com.onlinebetting.pojo.vo.BetOfferDetail;
import com.onlinebetting.service.SessionService;
import com.onlinebetting.service.StakeService;
import com.onlinebetting.web.annotation.Autowired;
import com.onlinebetting.web.annotation.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service(name = "sessionServiceImpl")
public class StakeServiceImpl implements StakeService {

    @Autowired
    private SessionService sessionService;

    private final Map<Long, BetOffer> betOffers = new ConcurrentHashMap<>();

    @Override
    public void offerStake(long betOfferId, String sessionKey, double amount) throws Exception {

        checkLegitimacyForAmount(amount);

        long customerId = sessionService.validateSession(sessionKey);

        BetOffer betOfferInfo = betOffers.computeIfPresent(betOfferId, (id, betOffer) -> betOffer.add(BetOfferDetail.of(customerId, amount)));

        if (null == betOfferInfo) {
            betOffers.computeIfAbsent(betOfferId, K -> new BetOffer());
            betOffers.computeIfPresent(betOfferId, (id, betOffer) -> betOffer.add(BetOfferDetail.of(customerId, amount)));
        }
    }

    private void checkLegitimacyForAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount is illegal.");
        }
    }

    @Override
    public String highStakes(long betOfferId) {

        BetOffer betOffer = betOffers.get(betOfferId);

        if (null == betOffer) {
            return Constant.EMPTY_STRING;
        }
        return betOffer.getBetOfferDetails().stream().map(BetOfferDetail::toString).collect(Collectors.joining(Constant.ENGLISH_COMMA));
    }
}
