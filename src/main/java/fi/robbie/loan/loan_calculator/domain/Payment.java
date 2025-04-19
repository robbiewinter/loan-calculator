package fi.robbie.loan.loan_calculator.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Payment {
    private int month;

    private double totalPayment;

    private double principalPayment;


    private double interestPayment;

    private double remainingBalance;

    public Payment(int month, double totalPayment, double principalPayment, double interestPayment, double remainingBalance) {
        this.month = month;
        this.totalPayment = round(totalPayment);
        this.principalPayment = round(principalPayment);
        this.interestPayment = round(interestPayment);
        this.remainingBalance = round(remainingBalance);
    }

    private double round(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public int getMonth() {
        return month;
    }

    public double getTotalPayment() {
        return totalPayment;
    }

    public double getPrincipalPayment() {
        return principalPayment;
    }

    public double getInterestPayment() {
        return interestPayment;
    }

    public double getRemainingBalance() {
        return remainingBalance;
    }
}
