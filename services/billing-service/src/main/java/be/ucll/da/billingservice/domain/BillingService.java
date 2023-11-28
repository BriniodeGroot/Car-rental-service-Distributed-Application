package be.ucll.da.billingservice.domain;

import org.springframework.stereotype.Component;

@Component
public class BillingService {

    private Integer billId = 0;

    // Returns -1 if  not successful and patient is not insured, accountId otherwise
    public Integer openBill(Integer userId) {
        if (Math.random() > 0.3) {
            return ++billId;
        }

        return -1;
    }

    public boolean closeBill(Integer billId) {
       return true;
    }
}
