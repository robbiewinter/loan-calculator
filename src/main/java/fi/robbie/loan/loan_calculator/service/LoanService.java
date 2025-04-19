package fi.robbie.loan.loan_calculator.service;

import fi.robbie.loan.loan_calculator.domain.Payment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class to handle loan calculations.
 * Provides methods to calculate loan schedules for both simple and compound interest.
 */
@Service
public class LoanService {

    /**
     * Calculates the loan payment schedule based on the calculation type.
     *
     * @param principal       The loan amount.
     * @param annualRate      The annual interest rate in percentages.
     * @param months          The loan term in months.
     * @param calculationType The type of interest calculation ("SIMPLE" or "COMPOUND").
     * @return A list of Payment objects representing the payment schedule.
     */
    public List<Payment> calculateLoanSchedule(double principal, double annualRate, int months, String calculationType) {
        if (principal <= 0 || annualRate < 0 || months <= 0) {
            throw new IllegalArgumentException("Invalid input values: principal, annualRate, and months must be positive.");
        }

        // Determine the calculation type and generate the schedule accordingly
        switch (calculationType.toUpperCase()) {
            case "SIMPLE":
                return calculateSimpleInterestSchedule(principal, annualRate, months);
            case "COMPOUND":
                return calculateCompoundInterestSchedule(principal, annualRate, months);
            default:
                throw new IllegalArgumentException("Unsupported calculation type: " + calculationType);
        }
    }

    private List<Payment> calculateSimpleInterestSchedule(double principal, double annualRate, int months) {
        List<Payment> schedule = new ArrayList<>();
        double totalInterest = principal * (annualRate / 100) * (months / 12.0); // Convert months to years
        double monthlyPayment = (principal + totalInterest) / months;
        double remainingBalance = principal;

        // Generate the schedule for each month
        for (int i = 1; i <= months; i++) {
            double interestPayment = totalInterest / months;
            double principalPayment = monthlyPayment - interestPayment;
            remainingBalance -= principalPayment;

            if (remainingBalance < 0) remainingBalance = 0;

            schedule.add(new Payment(i, monthlyPayment, principalPayment, interestPayment, remainingBalance));
        }

        return schedule;
    }

    private List<Payment> calculateCompoundInterestSchedule(double principal, double annualRate, int months) {
        List<Payment> schedule = new ArrayList<>();
        double monthlyRate = annualRate / 12 / 100;
        double monthlyPayment = (principal * monthlyRate) / (1 - Math.pow(1 + monthlyRate, -months));
        double remainingBalance = principal;

        // Generate the schedule for each month
        for (int i = 1; i <= months; i++) {
            double interestPayment = remainingBalance * monthlyRate;
            double principalPayment = monthlyPayment - interestPayment;
            remainingBalance -= principalPayment;

            if (remainingBalance < 0) remainingBalance = 0;

            schedule.add(new Payment(i, monthlyPayment, principalPayment, interestPayment, remainingBalance));
        }

        return schedule;
    }
}
