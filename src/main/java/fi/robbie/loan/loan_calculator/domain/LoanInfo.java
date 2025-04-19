package fi.robbie.loan.loan_calculator.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;

@Entity
public class LoanInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double loanAmount;
    private double interestRate;
    private int loanTerm;
    private String calculationType;
    private String loanName;
    private LocalDate startDate;

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

    public String getLoanName() {
        return loanName;
    }

    public void setLoanName(String loanName) {
        this.loanName = loanName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    // Calculates monthly payment based on the loan type and updated loan amount
    public double calculateMonthlyPayment() {
        double payment;
        switch (calculationType.toUpperCase()) {
            case "SIMPLE":
                payment = calculateSimpleInterest();
                break;
            case "COMPOUND":
                payment = calculateCompoundInterest();
                break;
            default:
                throw new IllegalArgumentException("Unsupported calculation type: " + calculationType);
        }
        return Math.round(payment * 100.0) / 100.0;
    }

    // Simple interest calculation
    private double calculateSimpleInterest() {
        double totalInterest = loanAmount * (interestRate / 100) * (loanTerm / 12.0); // Convert months to years
        return (loanAmount + totalInterest) / loanTerm; // Loan term is in months
    }

    // Compound interest calculation
    private double calculateCompoundInterest() {
        double monthlyRate = interestRate / 100 / 12;
        int totalPayments = loanTerm; // Loan term is already in months
        return loanAmount * Math.pow(1 + monthlyRate, totalPayments) / totalPayments;
    }
}
