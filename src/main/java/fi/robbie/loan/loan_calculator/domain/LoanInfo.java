package fi.robbie.loan.loan_calculator.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class LoanInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double loanAmount;
    private double interestRate;
    private int loanTerm;
    private String calculationType; // Loan type

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public int getLoanTerm() {
        return loanTerm;
    }

    public void setLoanTerm(int loanTerm) {
        this.loanTerm = loanTerm;
    }

    public String getCalculationType() {
        return calculationType;
    }

    public void setCalculationType(String calculationType) {
        this.calculationType = calculationType;
    }

    // Calculates monthly payment based on the loan type
    public double calculateMonthlyPayment() {
        double payment;
        switch (calculationType.toUpperCase()) {
            case "SIMPLE":
                payment = calculateSimpleInterest();
                break;
            case "COMPOUND":
                payment = calculateCompoundInterest();
                break;
            default: // Default is amortized loan
                payment = calculateAmortizedPayment();
        }
        return Math.round(payment * 100.0) / 100.0;
    }

    // Amortized loan calculation
    private double calculateAmortizedPayment() {
        double monthlyRate = interestRate / 100 / 12; // Convert yearly rate to monthly
        int totalPayments = loanTerm * 12; // Number of payments
        if (monthlyRate == 0) {
            return loanAmount / totalPayments; // If interest rate is 0
        }
        return loanAmount * (monthlyRate * Math.pow(1 + monthlyRate, totalPayments)) /
               (Math.pow(1 + monthlyRate, totalPayments) - 1);
    }

    // Simple interest calculation
    private double calculateSimpleInterest() {
        double totalInterest = loanAmount * (interestRate / 100) * loanTerm;
        return (loanAmount + totalInterest) / (loanTerm * 12);
    }

    // Compound interest calculation
    private double calculateCompoundInterest() {
        double monthlyRate = interestRate / 100 / 12; // Convert annual rate to monthly
        int totalPayments = loanTerm * 12; // Total number of payments
        return loanAmount * Math.pow(1 + monthlyRate, totalPayments) / totalPayments;
    }
}
