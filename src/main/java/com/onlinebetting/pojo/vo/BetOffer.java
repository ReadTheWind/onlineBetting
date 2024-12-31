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
        Optional<BetOfferDetail> customerLogOpt = getBetOfferDetails().stream().filter(log -> betOfferDetail.getCustomerId().equals(log.getCustomerId())).findAny();
        if (customerLogOpt.isPresent()) {
            BetOfferDetail detail = customerLogOpt.get();
            if (betOfferDetail.getStakeAmount() > detail.getStakeAmount()) {
                this.betOfferDetails.remove(detail);
                this.betOfferDetails.add(betOfferDetail);
            }
        } else {
            this.betOfferDetails.add(betOfferDetail);
        }
        if (getBetOfferDetails().size() > Constant.MAX_NUMBER) {
            getBetOfferDetails().pollLast();
        }
        return this;
    }


    public ConcurrentSkipListSet<BetOfferDetail> getBetOfferDetails() {
        return betOfferDetails;
    }
}
