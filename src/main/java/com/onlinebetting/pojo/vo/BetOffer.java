package com.onlinebetting.pojo.vo;

import com.onlinebetting.constant.Constant;

import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListSet;

public class BetOffer {

    /**
     * store top 20 bet offer logs
     */
    private ConcurrentSkipListSet<BetOfferDetail> betOfferDetails;

    public BetOffer() {
        betOfferDetails = new ConcurrentSkipListSet<>(
                Comparator.comparing(BetOfferDetail::getStakeAmount)
                        .thenComparing(BetOfferDetail::getCustomerId).reversed()
        );
    }

    public BetOffer add(BetOfferDetail betOfferDetail) {
        Optional<BetOfferDetail> customerPreviousBetOffer = findCustomerPreviousBetOffer(betOfferDetail);

        if (invalidBetOffer(betOfferDetail, customerPreviousBetOffer.orElse(null))) {
            return this;
        }

        customerPreviousBetOffer.ifPresent(previousOfferDetail -> this.betOfferDetails.remove(previousOfferDetail));

        this.betOfferDetails.add(betOfferDetail);

        doOverSizeCheck();

        return this;
    }

    private boolean invalidBetOffer(BetOfferDetail betOfferDetail, BetOfferDetail customerLastTimeBetOffer) {
        return null != customerLastTimeBetOffer && betOfferDetail.getStakeAmount() < customerLastTimeBetOffer.getStakeAmount();
    }

    private Optional<BetOfferDetail> findCustomerPreviousBetOffer(BetOfferDetail betOfferDetail) {
        return getBetOfferDetails()
                .stream()
                .filter(log -> betOfferDetail.getCustomerId().equals(log.getCustomerId()))
                .findAny();
    }

    private void doOverSizeCheck() {
        if (getBetOfferDetails().size() <= Constant.MAX_NUMBER) {
            return;
        }
        getBetOfferDetails().pollLast();
    }


    public ConcurrentSkipListSet<BetOfferDetail> getBetOfferDetails() {
        return betOfferDetails;
    }
}
