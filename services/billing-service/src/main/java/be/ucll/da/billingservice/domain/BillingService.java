package be.ucll.da.billingservice.domain;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

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

    public Integer calculateAmount(Integer price, LocalDate preferredStart, LocalDate preferredEnd) {
        long daysBetween = ChronoUnit.DAYS.between(preferredStart, preferredEnd);
        int result = Math.toIntExact(daysBetween * price);
        return result;
    }

//    public boolean closeBill(Integer billId) {
//       return true;
//    }
}
